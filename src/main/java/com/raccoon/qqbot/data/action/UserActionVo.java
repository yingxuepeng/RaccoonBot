package com.raccoon.qqbot.data.action;

import com.raccoon.qqbot.config.MiraiConfig;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.PlainText;

import java.nio.charset.StandardCharsets;

public class UserActionVo {
    private GroupMessageEvent event;
    private UserActionConsts.Type type;
    private long targetId;

    private String actionStr;


    public static UserActionVo From(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {

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
        if (actionStr.startsWith("看")) {
            type = UserActionConsts.Type.QUOTA_SHOW;
        } else if (actionStr.startsWith("干")) {
            type = UserActionConsts.Type.QUOTA_DECREASE;
        } else if (actionStr.startsWith("夸")) {
            type = UserActionConsts.Type.QUOTA_DECREASE;
        } else {
            return null;
        }
        UserActionVo userAction = new UserActionVo();
        userAction.event = event;
        userAction.targetId = target.getTarget();
        userAction.actionStr = actionStr;

        return userAction;
    }

    public GroupMessageEvent getEvent() {
        return event;
    }

    public void setEvent(GroupMessageEvent event) {
        this.event = event;
    }

    public UserActionConsts.Type getType() {
        return type;
    }

    public void setType(UserActionConsts.Type type) {
        this.type = type;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getActionStr() {
        return actionStr;
    }

    public void setActionStr(String actionStr) {
        this.actionStr = actionStr;
    }
}
