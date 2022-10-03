package com.raccoon.qqbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raccoon.qqbot.data.BotUserQuotaVo;
import com.raccoon.qqbot.data.MsgConfig;
import com.raccoon.qqbot.data.ScriptResultVo;
import com.raccoon.qqbot.data.action.*;
import com.raccoon.qqbot.data.msg.MsgChainVo;
import com.raccoon.qqbot.db.consts.BotAdminActionConsts;
import com.raccoon.qqbot.db.consts.BotMessageConsts;
import com.raccoon.qqbot.db.entity.BotAdminActionEntity;
import com.raccoon.qqbot.db.entity.BotGroupTopicEntity;
import com.raccoon.qqbot.db.entity.BotMessageEntity;
import com.raccoon.qqbot.db.entity.BotScriptEntity;
import com.raccoon.qqbot.exception.ReturnedException;
import com.raccoon.qqbot.exception.ServiceError;
import com.raccoon.qqbot.service.schedule.ScheduleService;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.nlp.v20190408.models.ClassificationResult;
import com.tencentcloudapi.nlp.v20190408.models.TextClassificationResponse;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GroupMsgService extends BaseService {
    @Autowired
    private ScheduleService scheduleService;

    public void showMemberQuota(MessageEvent event) {
        long memberId = Long.parseLong(event.getMessage().contentToString());
        ScriptResultVo info = getMemberMuteInfo(memberId);
        event.getSender().sendMessage(new PlainText(info.getMsgCnt() + "/" + info.getMsgQuota()));
    }

    public void showMyQuota(MessageEvent event) {
        long memberId = event.getSender().getId();
        ScriptResultVo info = getMemberMuteInfo(memberId);
        event.getSender().sendMessage(new PlainText(info.getMsgCnt() + "/" + info.getMsgQuota()));
    }

    public void showQuota(GroupMessageEvent event, QuotaShowAction userAction) {
        Long targetId = userAction.getTargetList().get(0).getTargetId();
        ScriptResultVo info = getMemberMuteInfo(targetId);
        List<BotAdminActionEntity> actionEntityList = botAdminActionDao.selectByMemberScriptStatus(targetId, 1L, BotAdminActionConsts.STATUS_NORMAL);
        QuotaShowAction.Stat stat = userAction.getStat(actionEntityList);
        final String cHeart = "\u2764";
        final String cBomb = "\uD83D\uDCA3";
        final String cLife = "\uD83D\uDC53";
        String quotaSumStr = cHeart + " x " + stat.getHeartCnt() + " " + cBomb + " x " + stat.getBombCnt() + " " + cLife + " x " + stat.getLifeCnt() + "\n";

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");
        String quotaDetailStr = "详情：";
        for (BotAdminActionEntity action : actionEntityList) {

            if (action.getType() == BotAdminActionConsts.TYPE_QUOTA) {
                if (action.getQuotaCnt() > 0) {
                    quotaDetailStr += "\n" + cHeart + " x " + action.getQuotaCnt() + " : " + format.format(new Date(action.getExpireTime().getTime()));
                } else if (action.getQuotaCnt() < 0) {
                    quotaDetailStr += "\n" + cBomb + " x " + (-action.getQuotaCnt()) + " : " + format.format(new Date(action.getExpireTime().getTime()));
                }
            } else if (action.getType() == BotAdminActionConsts.TYPE_QUOTA_EXTRA) {
                if (action.getQuotaCnt() > 0) {
                    quotaDetailStr += "\n" + cLife + " x " + action.getQuotaCnt() + " : " + format.format(new Date(action.getExpireTime().getTime()));
                }
            }
        }

        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new At(targetId));
        builder.append(new PlainText("今日发言次数为：" + info.getMsgCnt() + "/" + info.getMsgQuota() + "\n"));
        builder.append(new PlainText(quotaSumStr));
        builder.append(new PlainText(quotaDetailStr));
        event.getGroup().sendMessage(builder.build());
    }

    public void changeQuotaBatch(GroupMessageEvent event, QuotaChangeAction userAction) {
        userAction.getTargetList().forEach(target -> changeQuotaSingle(event, userAction, target.getTargetId()));
    }

    private void changeQuotaSingle(GroupMessageEvent event, QuotaChangeAction userAction, long targetId) {
        BotAdminActionEntity botAdminActionEntity = botAdminActionDao.selectByAdminMemberStatus(userAction.getSenderId(), targetId,
                BotAdminActionConsts.STATUS_NORMAL, BotAdminActionConsts.TYPE_QUOTA);
        // quota step

        //  quota change
        int deltaQuotaCnt = StringUtils.countOccurrencesOf(userAction.getActionStr(), userAction.getType().getKeyword());

        UserAction.Permission permission = userAction.getSenderPermission();
        if (permission.lessThan(UserAction.Permission.OWNER)) {
            // 限制码皇和管理员的单次扣除或者增加量
            deltaQuotaCnt = Math.min(3, deltaQuotaCnt);
        }

        if (userAction.getType() == UserAction.Type.QUOTA_DECREASE) {
            deltaQuotaCnt = -deltaQuotaCnt;
        }
        int curQuotaCnt = 0;
        boolean isUpdate = true;
        if (botAdminActionEntity == null) {
            isUpdate = false;
            botAdminActionEntity = botAdminActionDao.createEntity(userAction.getSenderId(), targetId, BotAdminActionConsts.TYPE_QUOTA);

            // expire time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DATE, 45);
            botAdminActionEntity.setExpireTime(new Timestamp(calendar.getTime().getTime()));
        } else {
            curQuotaCnt += botAdminActionEntity.getQuotaCnt();
        }
        // step
        int quotaStep = userAction.getStep();
        botAdminActionEntity.setQuotaStep(quotaStep);

        // quota cnt
        QuotaChangeAction.Range range = userAction.getRange();
        curQuotaCnt = Math.min(Math.max(range.getMin(), curQuotaCnt + deltaQuotaCnt), range.getMax());
        String hintStr = "";
        if (deltaQuotaCnt > 0) {
            hintStr = "夸夸夸~都可以夸！";
        } else {
            hintStr = "干干干~都可以干！";
        }
        if (isUpdate) {
            if (curQuotaCnt == botAdminActionEntity.getQuotaCnt()) {
                if (curQuotaCnt == range.getMin()) {
                    event.getGroup().sendMessage("干到底了，真得干不动了~");
                } else if (curQuotaCnt == range.getMax()) {
                    event.getGroup().sendMessage("夸上天了，真得夸不动了~");
                }
                return;
            }
            botAdminActionEntity.setQuotaCnt(curQuotaCnt);
            botAdminActionDao.updateById(botAdminActionEntity);
        } else {
            botAdminActionEntity.setQuotaCnt(curQuotaCnt);
            botAdminActionDao.insert(botAdminActionEntity);
        }
        sendQuotaInfo(event.getGroup(), hintStr, targetId);
        if (deltaQuotaCnt > 0) {
            NormalMember normalMember = event.getGroup().getMembers().get(targetId);
            if (normalMember.isMuted()) {
                normalMember.unmute();
            }
        }
    }


    public void addExtraLife(GroupMessageEvent event, QuotaExtraLifeAction userAction) {
        Long targetId = userAction.getTargetList().get(0).getTargetId();
        BotAdminActionEntity botAdminActionEntity = botAdminActionDao.selectByAdminMemberStatus(userAction.getSenderId(), targetId,
                BotAdminActionConsts.STATUS_NORMAL, BotAdminActionConsts.TYPE_QUOTA_EXTRA);
        // quota step

        int quotaStep = userAction.getStep();

        //  quota change
        int deltaQuotaCnt = StringUtils.countOccurrencesOf(userAction.getActionStr(), userAction.getType().getKeyword());

        int curQuotaCnt = 0;
        boolean isUpdate = true;
        if (botAdminActionEntity == null) {
            isUpdate = false;
            botAdminActionEntity = botAdminActionDao.createEntity(userAction.getSenderId(), targetId, BotAdminActionConsts.TYPE_QUOTA_EXTRA);

            // expire time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DATE, 1);
            botAdminActionEntity.setExpireTime(new Timestamp(calendar.getTime().getTime()));
        } else {
            curQuotaCnt += botAdminActionEntity.getQuotaCnt();
        }
        botAdminActionEntity.setQuotaStep(quotaStep);


        curQuotaCnt = Math.min((curQuotaCnt + deltaQuotaCnt), userAction.getMaxLifeCnt());
        String hintStr = "续续续~又续了命！";

        if (isUpdate) {
            if (curQuotaCnt == botAdminActionEntity.getQuotaCnt()) {
                if (curQuotaCnt == userAction.getMaxLifeCnt()) {
                    event.getGroup().sendMessage("续到头了，真得续不动了~");
                }
                return;
            }
            botAdminActionEntity.setQuotaCnt(curQuotaCnt);
            botAdminActionDao.updateById(botAdminActionEntity);
        } else {
            botAdminActionEntity.setQuotaCnt(curQuotaCnt);
            botAdminActionDao.insert(botAdminActionEntity);
        }
        NormalMember normalMember = event.getGroup().getMembers().get(targetId);
        if (normalMember.isMuted()) {
            normalMember.unmute();
        }
        sendQuotaInfo(event.getGroup(), hintStr, targetId);
    }

    private void sendQuotaInfo(Group group, String prefixStr, long memberId) {
        ScriptResultVo info = getMemberMuteInfo(memberId);
        MessageChainBuilder builder = new MessageChainBuilder();
        if (prefixStr != null) {
            builder.append(new PlainText(prefixStr));
        }
        builder.append(new At(memberId));
        builder.append(new PlainText("今日发言次数为：" + info.getMsgCnt() + "/" + info.getMsgQuota()));
        group.sendMessage(builder.build());
    }

    private ScriptResultVo getMemberMuteInfo(long memberId) {
        // 暂时写死
        final long scriptId = 1;
        HashMap<String, Object> data = new HashMap<>();
        {
            List<BotAdminActionEntity> actionList = botAdminActionDao.selectByMemberScriptStatus(memberId, scriptId, BotAdminActionConsts.STATUS_NORMAL);
            if (actionList == null) {
                actionList = new ArrayList<>();
            }
            data.put("actionList", actionList);
        }

        {
            List<BotMessageEntity> msgBriefList = botMessageDao.selectMessageBrief(memberId, getTodayMidnight());
            if (msgBriefList == null) {
                msgBriefList = new ArrayList<>();
            }
            data.put("msgBriefList", msgBriefList);
        }

        {
            MsgConfig msgConfig = new MsgConfig();
            msgConfig.setIsWeekend(false);
            msgConfig.setIsHoliday(false);

            Boolean isHoliday = redisService.getIsHoliday();
            if (isHoliday != null) {
                msgConfig.setIsHoliday(isHoliday);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    msgConfig.setIsWeekend(true);
                }
            }
            data.put("msgConfig", msgConfig);
        }

        BotScriptEntity botScriptEntity = botScriptDao.selectById(scriptId);
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
            System.out.println(dataStr);
            result = (String) invocable.invokeFunction(scriptEntity.getScriptEntrance(), dataStr);
            return result;
        } catch (ScriptException | NoSuchMethodException | JsonProcessingException e) {
            e.printStackTrace();
            throw new ReturnedException(ServiceError.JSEXEC_ERROR);
        }
    }


    public void saveMsg(GroupMessageEvent event) {
        long memberId = event.getSender().getId();
        BotMessageEntity entity = new BotMessageEntity();

        MessageSource source = (MessageSource) event.getMessage().get(0);
        int msgId = source.getIds()[0];
        entity.setSenderId(memberId);
        entity.setSenderNick(event.getSenderName());
        entity.setGroupId(event.getGroup().getId());
        entity.setMsgId((long) msgId);
        // msg save
        StringBuilder msgStr = new StringBuilder();
        Boolean isTrainableMsg = getGroupMsgString(event, msgStr);
        String msg = msgStr.toString();
        final int MAX_LENGTH = 10000;
        if (msg.length() > MAX_LENGTH) {
            msg = msg.substring(0, MAX_LENGTH);
        }

        if (isTrainableMsg) {
            if (handleRepeatMsg(event.getMessage().contentToString())) {
                isTrainableMsg = false;
                entity.setLabelCrtype(BotMessageConsts.LABEL_CRTYPE_RULE);
                entity.setLabelType(BotMessageConsts.LABELTYPE_NONE);
                entity.setLabelFirst("复读");
                entity.setLabelSecond("复读");
                entity.setLfPby(1f);
                entity.setLsPby(1f);
            }
        }
        entity.setContent(msg);
        // save origin msg for frontend
        MsgChainVo msgChainVo = MsgChainVo.Create(event.getMessage(), event.getGroup());
        try {
            entity.setMsgChainJstr(objectMapper.writeValueAsString(msgChainVo));
        } catch (JsonProcessingException e) {
            throw new ReturnedException(ServiceError.COMMON_CUSTOM_MESSAGE);
        }

        entity.setIsDel(false);
        entity.setIsTrainable(isTrainableMsg);
        botMessageDao.insert(entity);

        if (isTrainableMsg) {
            startMsgClassificationTask(entity.getId(), msg);
        }
    }

    private Boolean getGroupMsgString(GroupMessageEvent event, StringBuilder resultStr) {
        MessageChain msgChain = event.getMessage();

        int textCnt = 0;
        for (Message msg : msgChain) {
            if (msg instanceof PlainText) {
                PlainText text = (PlainText) msg;
                resultStr.append(text.contentToString().trim());
                textCnt++;
            } else if (msg instanceof At) {
                At at = (At) msg;
                resultStr.append(" [@").append(getMemberName(event, at.getTarget())).append("] ");
            } else if (msg instanceof AtAll) {
                resultStr.append(" [@全体成员] ");
            } else if (msg instanceof Face) {
                Face face = (Face) msg;
                resultStr.append(" " + face.contentToString() + " ");
            } else {
                continue;
            }
        }

        if (textCnt <= 0) {
            resultStr.append(msgChain.contentToString());
        }
        return textCnt > 0;
    }

    private boolean handleRepeatMsg(String msg) {
        boolean isRepeat = redisService.hasMsg(msg);
        redisService.putMsgKey(msg, 20);
        return isRepeat;
    }

    private String getMemberName(GroupMessageEvent event, long memberId) {
        NormalMember member = event.getGroup().get(memberId);
        if (member.getNameCard() != null && member.getNameCard().trim().length() > 0) {
            return member.getNameCard().trim();
        }
        if (member.getNick() != null) {
            return member.getNick();
        }
        return "";
    }

    public void checkQuota(GroupMessageEvent event) {
        if (event.getSender().getPermission() != MemberPermission.MEMBER) {
            return;
        }
        long memberId = event.getSender().getId();
        ScriptResultVo resultVo = getMemberMuteInfo(memberId);

        if (resultVo.getShouldMute()) {
            event.getSender().mute((int) (resultVo.getMuteMillis() / 1000));

            ScriptResultVo info = getMemberMuteInfo(memberId);
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append(new At(memberId));
            builder.append(new PlainText("今日发言次数为：" + info.getMsgCnt() + "/" + info.getMsgQuota() + "，早点睡觉觉吧~\n"));
            builder.append(Image.fromId("{1FC3D44A-6F98-6E13-2025-756013B51688}.jpg"));
            event.getGroup().sendMessage(builder.build());
        }
    }

    public void showMsgAllTop5(GroupMessageEvent event) {
        List<BotUserQuotaVo> userQuotaList = botMessageDao.selectMsgTop5(getTodayMidnight());
        String msg = "今日发言TOP5：\n";
        for (int i = 0; i < userQuotaList.size(); i++) {
            BotUserQuotaVo uq = userQuotaList.get(i);
            msg += i + "." + getMemberName(event, uq.getSenderId()) + " : " + uq.getMsgCnt() + "\n";
        }
        event.getGroup().sendMessage(msg);
    }

    public void showMsgRepeatTop5(GroupMessageEvent event) {
        List<BotUserQuotaVo> userQuotaList = botMessageDao.selectMsgLabelTop5(getTodayMidnight(), "复读");
        String msg = "今日复读TOP5：\n";
        for (int i = 0; i < userQuotaList.size(); i++) {
            BotUserQuotaVo uq = userQuotaList.get(i);
            msg += i + "." + getMemberName(event, uq.getSenderId()) + " : " + uq.getMsgCnt() + "\n";
        }
        event.getGroup().sendMessage(msg);
    }

    @Async
    private void startMsgClassificationTask(long msgId, String msg) {
        BotMessageEntity entity = botMessageDao.selectById(msgId);
        try {
            TextClassificationResponse resp = qCloudNlpService.getMsgLabel(msg);
            if (resp.getClasses().length <= 0) {
                return;
            }
            ClassificationResult clsResult = resp.getClasses()[0];
            entity.setLabelCrtype(BotMessageConsts.LABEL_CRTYPE_QQNLP);
            entity.setLabelType(BotMessageConsts.LABELTYPE_NONE);
            entity.setLabelFirst(clsResult.getFirstClassName());
            entity.setLabelSecond(clsResult.getSecondClassName());
            entity.setLfPby(clsResult.getFirstClassProbability());
            entity.setLsPby(clsResult.getSecondClassProbability());
        } catch (TencentCloudSDKException e) {
            entity.setIsTrainable(false);
        }
        botMessageDao.updateById(entity);
    }

    public void createTopic(GroupMessageEvent event, QuoteAction action) {
        int msgId = action.getQuoteReply().getSource().getIds()[0];
        BotMessageEntity messageEntity = botMessageDao.selectByMsgId(msgId);
        if (messageEntity == null) {
            return;
        }

        BotGroupTopicEntity botGroupTopicEntity = botGroupTopicDao.createEntity(messageEntity);
        botGroupTopicDao.insert(botGroupTopicEntity);

        event.getGroup().sendMessage(new PlainText("主题 “" + botGroupTopicEntity.getTitle()) + "” 已创建");
    }

    public void setIsHoliday(GroupMessageEvent event, HolidayAction action) {
        if (action.getType() == UserAction.Type.CONFIG_HOLIDAY) {
            redisService.setIsHoliday(true, action.getDays());
            event.getGroup().sendMessage("过节" + action.getDays() + "天啦~~~");
        } else if (action.getType() == UserAction.Type.CONFIG_WORK) {
            redisService.setIsHoliday(false, action.getDays());
            event.getGroup().sendMessage("上班" + action.getDays() + "天嘞...");
        }
    }

    // vote不锁，可能会有非原子性操作导致的问题？
    final static Map<NormalMember, Integer> VOTE_MAP = new ConcurrentHashMap<>();
    final static Map<Member, Set<Member>> ALREADY_VOTE_MAP = new ConcurrentHashMap<>();
    final private static Pattern VOTE_PATTERN = Pattern.compile("(\\d+)");

    public void vote(GroupMessageEvent event, UserAction userAction) {
        Group group = event.getGroup();
        Long targetId = userAction.getTargetList().get(0).getTargetId();

        NormalMember target = group.get(targetId);
        if (target.getPermission().equals(MemberPermission.ADMINISTRATOR) || target.getPermission().equals(MemberPermission.OWNER)) {
            // 做不到哇。
            return;
        }
        // 已经禁言了，不能接着伤害它。
        if (target.isMuted()) {
            group.sendMessage("已经禁言啦，行行好");
            return;
        }
        String s = userAction.getActionStr();
        Matcher matcher = VOTE_PATTERN.matcher(s);
        if (matcher.find()) {
            String numberStr = matcher.group(1);
            int count = Integer.parseInt(numberStr);
            Member member = event.getSender();
            if (member.equals(target)) {
                // 目前各个群没有隔离，这样可能导致跨群投票。
                if (VOTE_MAP.size() == 1) {
                    for (NormalMember value : VOTE_MAP.keySet()) {
                        target = value;
                        break;
                    }
                } else {
                    // 判断为投自己
                }
            }
            boolean privilege = !userAction.getSenderPermission().lessThan(UserAction.Permission.CODING_EMPEROR);
            vote0(member, target, count, privilege, group);
        } else {
            group.sendMessage("请指定投票票数.");
        }
    }

    /**
     * 显示已有投票
     *
     * @param event
     * @param userAction
     */
    public void showVotes(GroupMessageEvent event, UserAction userAction) {
        Long targetId = userAction.getTargetList().get(0).getTargetId();
        Member target = event.getGroup().get(targetId);
        int votes = VOTE_MAP.getOrDefault(target, 0);
        event.getGroup().sendMessage("该用户被投了" + votes + "票.");
    }

    private final Map<Member, Future<?>> futureMap = new ConcurrentHashMap<>();

    private void vote0(Member fromMember, NormalMember toMember, int count, boolean privilege, Group group) {
        if (count == 0) {
            return;
        }
        // 权限校验
        int c = Math.abs(count);
        // 管理员有两票
        if (privilege) {
            if (c > 2) {
                int vote = VOTE_MAP.getOrDefault(toMember, 0);
                // 使用了累加计票的表示形式，例如当前总票数为 8/5, 投10/5
                if (count - vote != 2) {
                    group.sendMessage("票数和权限不符。");
                    return;
                } else {
                    count = 2;
                }
            }
        } else {
            if (c > 1) {
                int vote = VOTE_MAP.getOrDefault(toMember, 0);
                if (count - vote != 1) {
                    group.sendMessage("票数和权限不符。");
                    return;
                } else {
                    count = 1;
                }
            }
        }
        Set<Member> votedSet = ALREADY_VOTE_MAP.get(toMember);
        if (votedSet == null) {
            // 这里预示是第一次发生投票，因此需要保存future, 新的投票不延后这个执行时间
            votedSet = new HashSet<>();
            ALREADY_VOTE_MAP.put(toMember, votedSet);
            // 这里添加一个定时任务，然后在execute0时一并cancel掉;
            Future<?> future = scheduleService.schedule(() -> {
                        boolean success = execute0(group, toMember);
                        // 若结算不成功，则手动移除掉关联的所有记录
                        if (!success) {
                            VOTE_MAP.remove(toMember);
                            ALREADY_VOTE_MAP.remove(toMember);
                            futureMap.remove(toMember);
                        }
                    },
                    1, TimeUnit.HOURS);
            futureMap.put(toMember, future);
        } else {
            boolean absent = votedSet.add(fromMember);
            if (absent) {
                VOTE_MAP.merge(toMember, count, Integer::sum);
            }
        }
    }

    private final static int MUTE_LIMIT = 5;
    private final static int COUNT_INTERVAL = 5;

    /**
     * 执行票数结算
     *
     * @param group
     * @param member
     * @return 是否成功
     */
    private boolean execute0(Group group, Member member) {
        Integer votes = VOTE_MAP.getOrDefault(member, 0);
        // 票数阶梯性生效，每隔5票效果翻倍。
        if (votes >= MUTE_LIMIT) {
            int time = 0;
            int base = 1;
            while (votes > COUNT_INTERVAL) {
                time += base * COUNT_INTERVAL;
                votes -= COUNT_INTERVAL;
                base <<= 1;
            }
            time += votes * base;
            member.mute(time * 60);
            VOTE_MAP.remove(member);
            ALREADY_VOTE_MAP.remove(member);
            Future<?> future = futureMap.remove(member);
            future.cancel(false);
            return true;
        } else {
            group.sendMessage("未满足禁言条件，当前对象的投票数量为:" + votes);
            return false;
        }
    }

    /**
     * 计票执行
     *
     * @param event
     * @param userAction
     */
    public void execute(GroupMessageEvent event, UserAction userAction) {
        Group group = event.getGroup();
        Long targetId = userAction.getTargetList().get(0).getTargetId();
        Member target = group.get(targetId);
        execute0(group, target);
    }
}
