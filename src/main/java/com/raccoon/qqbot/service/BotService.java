package com.raccoon.qqbot.service;

//import com.raccoon.qqbot.db.dao.SolutionDao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raccoon.qqbot.cache.RedisService;
import com.raccoon.qqbot.config.MiraiConfig;
import com.raccoon.qqbot.data.ScriptResultVo;
import com.raccoon.qqbot.db.consts.BotAdminActionConsts;
import com.raccoon.qqbot.db.dao.BotAdminActionDao;
import com.raccoon.qqbot.db.dao.BotScriptDao;
import com.raccoon.qqbot.db.dao.BotUsedInvcodeDao;
import com.raccoon.qqbot.db.dao.SolutionDao;
import com.raccoon.qqbot.db.entity.BotAdminActionEntity;
import com.raccoon.qqbot.db.entity.BotScriptEntity;
import com.raccoon.qqbot.db.entity.BotUsedInvcodeEntity;
import com.raccoon.qqbot.db.entity.SolutionEntity;
import com.raccoon.qqbot.exception.ReturnedException;
import com.raccoon.qqbot.exception.ServiceError;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BotService {
    // json
    private ObjectMapper objectMapper;

    // mirai data
    @Autowired
    private Bot miraiBot;
    @Autowired
    private MiraiConfig.MiraiInfo miraiInfo;

    // service
    @Autowired
    private RedisService redisService;
    // dao
    @Resource
    private SolutionDao solutionDao;
    @Resource
    private BotAdminActionDao botAdminActionDao;
    @Resource
    private BotUsedInvcodeDao botUsedInvcodeDao;
    @Resource
    private BotScriptDao botScriptDao;

    public void handleJoinRequest(MemberJoinRequestEvent event) {
        String[] split = event.getMessage().trim().split("答案：");
        if (split.length < 2) {
            event.reject(false, "bot邀请码没查到,#开头转人工");
            return;
        }
        String invCode = split[1];
        if (invCode.startsWith("#")) {
            return;
        }
        SolutionEntity solutionEntity = solutionDao.getByUuid(invCode);
        if (solutionEntity == null) {
            event.reject(false, "bot邀请码没查到,#开头转人工");
            return;
        }

        BotUsedInvcodeEntity botUsedInvcodeEntity = botUsedInvcodeDao.selectByInvcode(invCode);
        if (botUsedInvcodeEntity == null) {
            botUsedInvcodeEntity = new BotUsedInvcodeEntity();
            botUsedInvcodeEntity.setIsDel(false);
            botUsedInvcodeEntity.setMemberId(event.getFromId());
            botUsedInvcodeEntity.setInvcode(invCode);
            botUsedInvcodeEntity.setSolutionId(solutionEntity.getSolutionId());
            botUsedInvcodeDao.insert(botUsedInvcodeEntity);
        } else {
            // 使用的是已经验证过的邀请码，暂时不处理
        }
    }

    public void sendWelcomeMessage(MemberJoinEvent event) {
        SolutionEntity solutionEntity = getNewMemberSolutionEntity(event.getMember().getId());
        String title = getMemberTitle(0);
        if (solutionEntity != null) {
            title = getMemberTitle(solutionEntity.getProblemId());
        }
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new PlainText("欢迎新" + title + "! "));
        builder.append(new At(event.getMember().getId()));
        builder.append(new PlainText("\n请熟读群公告规定，并修改群名片为：'{昵称}_{最好的语言}'!"));
        miraiBot.getGroup(miraiInfo.getGroupId()).sendMessage(builder.build());


        if (solutionEntity == null) {
            return;
        }
        // url
        String url = "新群友代码地址：\n" + "http://www.primeoj.com/uuid.php?uuid=" + solutionEntity.getSolutionUuid();
        miraiBot.getGroup(miraiInfo.getGroupId()).sendMessage(new PlainText(url));
    }

    private SolutionEntity getNewMemberSolutionEntity(long qid) {
        BotUsedInvcodeEntity botUsedInvcodeEntity = botUsedInvcodeDao.selectByMemberId(qid);
        if (botUsedInvcodeEntity == null) {
            return null;
        }

        return solutionDao.selectById(botUsedInvcodeEntity.getSolutionId());
    }

    private String getMemberTitle(int problemId) {
        if (problemId == 1000) {
            return "码皇";
        } else if (problemId == 1001) {
            return "码猴";
        } else if (problemId == 1002) {
            return "码农";
        }
        return "关系户";
    }

    public void handleAdminMsg(GroupMessageEvent event) {
        if (event.getMessage().size() < 4) {
            return;
        }
        if (!(event.getMessage().get(1) instanceof At)) {
            return;
        }
        At me = (At) event.getMessage().get(1);
        if (me.getTarget() != miraiInfo.getBotId()) {
            return;
        }
        if (!(event.getMessage().get(2) instanceof PlainText)) {
            return;
        }
        PlainText action = (PlainText) event.getMessage().get(2);
        String muteStr = new String("干他".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        String infoStr = new String("看看配额".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        String forgiveStr = new String("+1命".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        if (!(action.getContent().contains(infoStr) || action.getContent().contains(muteStr) || action.getContent().contains(forgiveStr))) {
            return;
        }
        if (!(event.getMessage().get(3) instanceof At)) {
            return;
        }
        At target = (At) event.getMessage().get(3);
        long memberId = target.getTarget();
        MemberPermission permission = event.getGroup().get(memberId).getPermission();
        if (permission == MemberPermission.ADMINISTRATOR || permission == MemberPermission.OWNER) {
            event.getGroup().sendMessage("权限不足，小浣熊哭哭");
            return;
        }

        if (action.getContent().contains(muteStr)) {
            addMuteRule(event, event.getSender().getId(), memberId);
        } else if (action.getContent().contains(infoStr)) {
            printMuteInfo(event, memberId);
        } else if (action.getContent().contains(forgiveStr)) {
            redisService.clearMsgTimeList(memberId);
            event.getGroup().get(target.getTarget()).unmute();

            ScriptResultVo info = getMemberMuteInfo(memberId);
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append(new At(memberId));
            builder.append(new PlainText("你又续了一条命，当前发言次数已清零：" + info.getMsgCnt() + "/" + info.getMsgLimitCnt()));
            event.getGroup().sendMessage(builder.build());
        }
    }

    private void addMuteRule(GroupMessageEvent event, long adminId, long memberId) {
        BotAdminActionEntity botAdminActionEntity = botAdminActionDao.selectByAdminUserStatus(adminId, memberId, BotAdminActionConsts.STATUS_NORMAL);
        if (botAdminActionEntity != null) {
            event.getGroup().sendMessage("~已经在干了，不能重复干~");
            return;
        }

        botAdminActionEntity = new BotAdminActionEntity();
        botAdminActionEntity.setAdminId(adminId);
        botAdminActionEntity.setMemberId(memberId);
        botAdminActionEntity.setScriptId(1L);
        botAdminActionEntity.setStatus(BotAdminActionConsts.STATUS_NORMAL);
        botAdminActionEntity.setType(BotAdminActionConsts.TYPE_QUOTA);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DATE, 7);
        LocalDateTime localDateTime = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        botAdminActionEntity.setExpireTime(localDateTime);
        botAdminActionEntity.setIsDel(false);
        botAdminActionDao.insert(botAdminActionEntity);
        event.getGroup().sendMessage("~干干干，干起来，都可以干~");
        printMuteInfo(event, memberId);
    }

    private void printMuteInfo(GroupMessageEvent event, long memberId) {
        ScriptResultVo info = getMemberMuteInfo(memberId);
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new At(memberId));
        builder.append(new PlainText("今日发言次数为：" + info.getMsgCnt() + "/" + info.getMsgLimitCnt()));
        event.getGroup().sendMessage(builder.build());
    }

    public void saveMemberMsg(GroupMessageEvent event) {
        long memberId = event.getSender().getId();
        redisService.putMsgTime(memberId);
        List<Long> memberMsgList = redisService.getMemberMsgTimeList(memberId);
        if (memberMsgList.size() > 0) {
        }
    }

    public void handleMemberMsg(GroupMessageEvent event) {
        long memberId = event.getSender().getId();
        ScriptResultVo resultVo = getMemberMuteInfo(memberId);

        if (resultVo.getShouldMute()) {
            event.getSender().mute((int) (resultVo.getMuteMillis() / 1000));

            ScriptResultVo info = getMemberMuteInfo(memberId);
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append(new At(memberId));
            builder.append(new PlainText("今日发言次数为：" + info.getMsgCnt() + "/" + info.getMsgLimitCnt() + "，早点睡觉觉吧~\n"));
            builder.append(Image.fromId("{1FC3D44A-6F98-6E13-2025-756013B51688}.jpg"));
            event.getGroup().sendMessage(builder.build());
        }
    }

    public void showMemberQuota(MessageEvent event) {
        long memberId = Long.parseLong(event.getMessage().contentToString());
        ScriptResultVo info = getMemberMuteInfo(memberId);
        event.getSender().sendMessage(new PlainText(info.getMsgCnt() + "/" + info.getMsgLimitCnt()));
    }

    public void showMyQuota(MessageEvent event) {
        long memberId = event.getSender().getId();
        ScriptResultVo info = getMemberMuteInfo(memberId);
        event.getSender().sendMessage(new PlainText(info.getMsgCnt() + "/" + info.getMsgLimitCnt()));
    }

    private ScriptResultVo getMemberMuteInfo(long memberId) {
        // 暂时写死
        final long scriptId = 1;
        List<BotAdminActionEntity> actionList = botAdminActionDao.selectByMemberScriptStatus(memberId, scriptId, BotAdminActionConsts.STATUS_NORMAL);
        BotScriptEntity botScriptEntity = botScriptDao.selectById(scriptId);
        List<Long> msgTimeList = redisService.getMemberMsgTimeList(memberId);
        HashMap<String, Object> data = new HashMap<>();
        data.put("actionList", actionList);
        data.put("msgTimeList", msgTimeList);
        String resultStr = runFileScript(botScriptEntity, data);
        try {
            return objectMapper.readValue(resultStr, ScriptResultVo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String runFileScript(BotScriptEntity scriptEntity, Map<String, Object> data) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine jsEngine = manager.getEngineByName("JavaScript");

        String resource = scriptEntity.getScriptUrl();
        String result;
        try {
            jsEngine.eval(new InputStreamReader(getClass().getResourceAsStream(resource)));
            Invocable invocable = (Invocable) jsEngine;

            String dataStr = objectMapper.writeValueAsString(data);
            result = (String) invocable.invokeFunction(scriptEntity.getScriptEntrance(), dataStr);
            return result;
        } catch (ScriptException e) {
            throw new ReturnedException(ServiceError.JSEXEC_ERROR);
        } catch (NoSuchMethodException e) {
            throw new ReturnedException(ServiceError.JSEXEC_ERROR);
        } catch (JsonProcessingException e) {
            throw new ReturnedException(ServiceError.JSEXEC_ERROR);
        } catch (IOException e) {
            throw new ReturnedException(ServiceError.JSEXEC_ERROR);
        }
    }
}
