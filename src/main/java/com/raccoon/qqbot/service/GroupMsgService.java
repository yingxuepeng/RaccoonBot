package com.raccoon.qqbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raccoon.qqbot.data.BotUserQuotaVo;
import com.raccoon.qqbot.data.ScriptResultVo;
import com.raccoon.qqbot.data.action.QuotaChangeAction;
import com.raccoon.qqbot.data.action.QuotaExtraLifeAction;
import com.raccoon.qqbot.data.action.QuotaShowAction;
import com.raccoon.qqbot.data.action.UserAction;
import com.raccoon.qqbot.db.consts.BotAdminActionConsts;
import com.raccoon.qqbot.db.consts.BotMessageConsts;
import com.raccoon.qqbot.db.entity.BotAdminActionEntity;
import com.raccoon.qqbot.db.entity.BotMessageEntity;
import com.raccoon.qqbot.db.entity.BotScriptEntity;
import com.raccoon.qqbot.exception.ReturnedException;
import com.raccoon.qqbot.exception.ServiceError;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupMsgService extends BaseService {

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
        ScriptResultVo info = getMemberMuteInfo(userAction.getTargetId());
        List<BotAdminActionEntity> actionEntityList = botAdminActionDao.selectByMemberScriptStatus(userAction.getTargetId(), 1L, BotAdminActionConsts.STATUS_NORMAL);
        QuotaShowAction.Stat stat = userAction.getStat(actionEntityList);
        final String cHeart = "\u2764";
        final String cBomb = "\uD83D\uDCA3";
        String quotaSumStr = "加减：";
        if (stat.getHeartCnt() > stat.getBombCnt()) {
            for (int i = 0; i < stat.getHeartCnt() - stat.getBombCnt(); i++) {
                quotaSumStr += cHeart;
            }
        } else if (stat.getHeartCnt() < stat.getBombCnt()) {
            for (int i = 0; i < stat.getBombCnt() - stat.getHeartCnt(); i++) {
                quotaSumStr += cBomb;
            }
        } else {
            quotaSumStr += "无";
        }

        String quotaDetailStr = "详情：" + cHeart + "x" + stat.getHeartCnt() + " , " + cBomb + "x" + stat.getBombCnt();
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new At(userAction.getTargetId()));
        builder.append(new PlainText("今日发言次数为：" + info.getMsgCnt() + "/" + info.getMsgQuota() + "\n"));
        builder.append(new PlainText(quotaSumStr + "\n"));
        builder.append(new PlainText(quotaDetailStr + "\n"));
        event.getGroup().sendMessage(builder.build());
    }

    public void changeQuota(GroupMessageEvent event, QuotaChangeAction userAction) {
        // 无法禁言管理员，所以不用操作quota
        if (!userAction.getTargetPermission().lessThan(UserAction.Permission.ADMINISTRATOR)) {
            sendNoPermissionMessage(event.getGroup());
            return;
        }
        BotAdminActionEntity botAdminActionEntity = botAdminActionDao.selectByAdminMemberStatus(userAction.getSenderId(), userAction.getTargetId(),
                BotAdminActionConsts.STATUS_NORMAL, BotAdminActionConsts.TYPE_QUOTA);
        // quota step


        //  quota change
        int deltaQuotaCnt = StringUtils.countOccurrencesOf(userAction.getActionStr(), userAction.getType().getKeyword());
        if (userAction.getType() == UserAction.Type.QUOTA_DECREASE) {
            deltaQuotaCnt = -deltaQuotaCnt;
        }
        int curQuotaCnt = 0;
        boolean isUpdate = true;
        if (botAdminActionEntity == null) {
            isUpdate = false;
            botAdminActionEntity = botAdminActionDao.createEntity(userAction.getSenderId(), userAction.getTargetId(), BotAdminActionConsts.TYPE_QUOTA);
        } else {
            curQuotaCnt += botAdminActionEntity.getQuotaCnt();
        }
        // step
        int quotaStep = userAction.getStep();
        botAdminActionEntity.setQuotaStep(quotaStep);

        // expire time，延长2个月
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MONTH, 2);
        LocalDateTime localDateTime = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        botAdminActionEntity.setExpireTime(localDateTime);

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
        sendQuotaInfo(event.getGroup(), hintStr, userAction.getTargetId());
    }

    public void addExtraLife(GroupMessageEvent event, QuotaExtraLifeAction userAction) {
        // 无法禁言管理员，所以不用操作quota
        if (!userAction.getTargetPermission().lessThan(UserAction.Permission.ADMINISTRATOR)) {
            sendNoPermissionMessage(event.getGroup());
            return;
        }
        BotAdminActionEntity botAdminActionEntity = botAdminActionDao.selectByAdminMemberStatus(userAction.getSenderId(), userAction.getTargetId(),
                BotAdminActionConsts.STATUS_NORMAL, BotAdminActionConsts.TYPE_QUOTA_EXTRA);
        // quota step

        int quotaStep = userAction.getStep();

        //  quota change
        int deltaQuotaCnt = StringUtils.countOccurrencesOf(userAction.getActionStr(), userAction.getType().getKeyword());

        int curQuotaCnt = 0;
        boolean isUpdate = true;
        if (botAdminActionEntity == null) {
            isUpdate = false;
            botAdminActionEntity = botAdminActionDao.createEntity(userAction.getSenderId(), userAction.getTargetId(), BotAdminActionConsts.TYPE_QUOTA_EXTRA);

            // expire time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DATE, 1);
            LocalDateTime localDateTime = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            botAdminActionEntity.setExpireTime(localDateTime);
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
        NormalMember normalMember = event.getGroup().getMembers().get(userAction.getTargetId());
        if (normalMember.isMuted()) {
            normalMember.unmute();
        }
        sendQuotaInfo(event.getGroup(), hintStr, userAction.getTargetId());
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
        List<BotAdminActionEntity> actionList = botAdminActionDao.selectByMemberScriptStatus(memberId, scriptId, BotAdminActionConsts.STATUS_NORMAL);
        BotScriptEntity botScriptEntity = botScriptDao.selectById(scriptId);
        List<BotMessageEntity> msgBriefList = botMessageDao.selectMessageBrief(memberId, getTodayMidnight());
        HashMap<String, Object> data = new HashMap<>();
        data.put("actionList", actionList);
        data.put("msgBriefList", msgBriefList);
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


    public void saveMsg(GroupMessageEvent event) {
        long memberId = event.getSender().getId();
        BotMessageEntity entity = new BotMessageEntity();

        // msg save
        StringBuilder msgStr = new StringBuilder();
        Boolean isTrainableMsg = getGroupMsgString(event, msgStr);
        String msg = msgStr.toString();
        final int MAX_LENGTH = 10000;
        if (msg.length() > MAX_LENGTH) {
            msg.substring(0, MAX_LENGTH);
        }

        if (isTrainableMsg) {
            if (handleRepeatMsg(msg)) {
                isTrainableMsg = false;
                entity.setLabelCrtype(BotMessageConsts.LABEL_CRTYPE_RULE);
                entity.setLabelType(BotMessageConsts.RULETYPE_NONE);
                entity.setLabelFirst("复读");
                entity.setLabelSecond("复读");
            }
        }

        entity.setSenderId(memberId);
        entity.setContent(msg);
        entity.setIsDel(false);
        entity.setIsTrainable(isTrainableMsg);
        botMessageDao.insert(entity);
    }

    private Boolean getGroupMsgString(GroupMessageEvent event, StringBuilder resultStr) {
        MessageChain msgChain = event.getMessage();

        int msgCnt = 0;
        for (Message msg : msgChain) {

            if (msg instanceof PlainText) {
                PlainText text = (PlainText) msg;
                resultStr.append(text.contentToString().trim());
            } else if (msg instanceof Image) {
                Image image = (Image) msg;
                resultStr.append(" [").append(image.getImageId()).append("] ");
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
            msgCnt++;
        }

        if (msgCnt <= 0) {
            resultStr.append(msgChain.contentToString());
        }
        return msgCnt > 0;
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

    public void showMsgTop5(GroupMessageEvent event) {
        List<BotUserQuotaVo> userQuotaList = botMessageDao.selectDailyTop5(getTodayMidnight());
        String msg = "今日发言TOP5：\n";
        for (int i = 0; i < userQuotaList.size(); i++) {
            BotUserQuotaVo uq = userQuotaList.get(i);
            msg += i + "." + getMemberName(event, uq.getSenderId()) + " : " + uq.getMsgCnt() + "\n";
        }
        event.getGroup().sendMessage(msg);
    }
}
