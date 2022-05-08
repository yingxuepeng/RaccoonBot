package com.raccoon.qqbot.data.action;

import com.raccoon.qqbot.config.MiraiConfig;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.PlainText;

public class UserAction {

    private UserActionConsts.Type type;
    private long senderId;
    private long targetId;

    private UserActionConsts.Permission senderPermission;
    private UserActionConsts.Permission targetPermission;
    private String actionStr;


    public static UserAction From(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {

        if (event.getMessage().size() < 4) {
            return null;
        }
        if (!(event.getMessage().get(1) instanceof At)) {
            return null;
        }
        // first is bot
        At me = (At) event.getMessage().get(1);
        if (me.getTarget() != miraiInfo.getBotId()) {
            return null;
        }
        // third is target
        if (!(event.getMessage().get(3) instanceof At)) {
            return null;
        }
        At target = (At) event.getMessage().get(3);

        // action
        if (!(event.getMessage().get(2) instanceof PlainText)) {
            return null;
        }
        PlainText action = (PlainText) event.getMessage().get(2);
        UserActionConsts.Type type = UserActionConsts.Type.NONE;
        String actionStr = action.getContent().trim();
        for (UserActionConsts.Type value : UserActionConsts.Type.values()) {
            if (value.getKeyword() == null) {
                continue;
            }
            if (actionStr.startsWith(value.getKeyword())) {
                type = value;
                break;
            }
        }

        if (type == UserActionConsts.Type.NONE) {
            return null;
        }

        UserAction userAction = new UserAction();
        userAction.type = type;
        userAction.senderId = event.getSender().getId();
        userAction.targetId = target.getTarget();
        userAction.senderPermission = GetPermission(event.getSender());
        userAction.targetPermission = GetPermission(event.getGroup().get(target.getTarget()));
        userAction.actionStr = actionStr;

        return userAction;
    }

    public static UserActionConsts.Permission GetPermission(Member member) {
        UserActionConsts.Permission memberPermission = UserActionConsts.Permission.MEMBER;
        if (member.getPermission() == MemberPermission.ADMINISTRATOR) {
            memberPermission = UserActionConsts.Permission.ADMINISTRATOR;
        } else if (member.getPermission() == MemberPermission.OWNER) {
            memberPermission = UserActionConsts.Permission.OWNER;
        } else if (member.getSpecialTitle().contains("\uD83D\uDC51")) {
            memberPermission = UserActionConsts.Permission.CODING_EMPEROR;
        }
        return memberPermission;

    }

    public UserActionConsts.Type getType() {
        return type;
    }

    public void setType(UserActionConsts.Type type) {
        this.type = type;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public UserActionConsts.Permission getSenderPermission() {
        return senderPermission;
    }

    public void setSenderPermission(UserActionConsts.Permission senderPermission) {
        this.senderPermission = senderPermission;
    }

    public UserActionConsts.Permission getTargetPermission() {
        return targetPermission;
    }

    public void setTargetPermission(UserActionConsts.Permission targetPermission) {
        this.targetPermission = targetPermission;
    }

    public String getActionStr() {
        return actionStr;
    }

    public void setActionStr(String actionStr) {
        this.actionStr = actionStr;
    }
}
