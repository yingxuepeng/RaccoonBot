package com.raccoon.qqbot.data.action;

import com.raccoon.qqbot.config.MiraiConfig;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.ArrayList;
import java.util.List;

public class UserAction {
    private ActionType type;
    private long senderId;

    private List<AtTarget> targetList;

    private Permission senderPermission;
    private String actionStr;


    public static UserAction From(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {
        if (event.getMessage().size() < 2) {
            return null;
        }
        if ((event.getMessage().get(1) instanceof At)) {
            return GetAtAction(event, miraiInfo);
        } else if ((event.getMessage().get(1) instanceof QuoteReply)) {
            return GetQuoteAction(event, miraiInfo);
        }
        return null;
    }

    public static UserAction GetAtAction(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {
        if (event.getMessage().size() < 3) {
            return null;
        }
        // user action
        UserAction userAction = null;
        // first should at bot
        At me = (At) event.getMessage().get(1);
        if (me.getTarget() != miraiInfo.getBotId()) {
            return null;
        }

        // second is action
        if (!(event.getMessage().get(2) instanceof PlainText)) {
            return null;
        }
        PlainText action = (PlainText) event.getMessage().get(2);
        String actionStr = action.getContent().trim().toLowerCase();

        ActionType type = GetType(actionStr);
        switch (type) {
            case NONE:
                break;
            case QUOTA_SHOW:
                userAction = new QuotaShowAction();
                break;
            case QUOTA_INCREASE:
            case QUOTA_DECREASE:
                userAction = new QuotaChangeAction();
                break;
            case QUOTA_EXTRALIFE_ADD:
                userAction = new QuotaExtraLifeAction();
                break;
            case CONFIG_HOLIDAY:
            case CONFIG_WORK:
                userAction = new HolidayAction();
                break;
            case MSG_ALL_TOP5:
            case MSG_REPEAT_TOP5:
            case SMACK:
            case TOPIC_LIST:
            case VOTE:
            case VOTE_COUNT:
            case EXECUTION:
                userAction = new UserAction();
                break;
            default:
                break;
        }

        // third++ is target
        List<AtTarget> targetList = new ArrayList<>();


        if (event.getMessage().size() == 3) {
            // target is self
            AtTarget atTarget = new AtTarget();
            atTarget.setTargetId(event.getSender().getId());
            atTarget.setTargetPermission(GetPermission(event.getGroup().get(atTarget.getTargetId())));
            targetList.add(atTarget);
        } else if (event.getMessage().size() > 3) {
            // add target id
            for (int i = 3; i < event.getMessage().size(); i++) {
                SingleMessage singleMessage = event.getMessage().get(i);
                if (singleMessage instanceof At) {
                    At target = (At) singleMessage;

                    AtTarget atTarget = new AtTarget();
                    atTarget.setTargetId(target.getTarget());
                    atTarget.setTargetPermission(GetPermission(event.getGroup().get(atTarget.getTargetId())));
                    targetList.add(atTarget);
                }
            }
        }
        if (targetList.size() <= 0) {
            // no target
            return null;
        }

        if (userAction != null) {
            userAction.type = type;
            userAction.senderId = event.getSender().getId();
            userAction.targetList = targetList;
            userAction.senderPermission = GetPermission(event.getSender());
            userAction.actionStr = actionStr;
        }

        return userAction;
    }

    public static UserAction GetQuoteAction(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {
        if (event.getMessage().size() < 4) {
            return null;
        }
        // first is bot
        QuoteReply quoteReply = (QuoteReply) event.getMessage().get(1);

        // at me
        if (!(event.getMessage().get(2) instanceof At)) {
            return null;
        }
        At me = (At) event.getMessage().get(2);
        if (me.getTarget() != miraiInfo.getBotId()) {
            return null;
        }

        // action
        if (!(event.getMessage().get(3) instanceof PlainText)) {
            return null;
        }
        PlainText action = (PlainText) event.getMessage().get(3);
        String actionStr = action.getContent().trim().toLowerCase();
        ActionType type = GetType(actionStr);

        long targetId = quoteReply.getSource().getIds()[0];
        // user action
        QuoteAction quoteAction = null;
        switch (type) {

            case TOPIC_CREATE:
                quoteAction = new QuoteAction();
                break;
            default:
                break;
        }
        if (quoteAction != null) {
            quoteAction.setType(type);
            quoteAction.setSenderId(event.getSender().getId());
            quoteAction.setQuoteReply(quoteReply);
            quoteAction.setActionStr(actionStr);
            quoteAction.setSenderPermission(GetPermission(event.getSender()));
        }

        return quoteAction;
    }

    /**
     * 在这里判断该Str的具体类型
     *
     * @param actionStr
     * @return
     */
    public static ActionType GetType(String actionStr) {
        // get action type
        ActionType type = ActionType.NONE;
        //  equal first
        for (ActionType value : ActionType.values()) {
            if (value.getKeywordMatchType() != KeywordMatchType.EQUAL) {
                continue;
            }
            if (actionStr.equals(value.getKeyword())) {
                type = value;
                break;
            }
        }
        // is not then  start with
        if (type == ActionType.NONE) {
            for (ActionType value : ActionType.values()) {
                if (value.getKeywordMatchType() != KeywordMatchType.START_WITH) {
                    continue;
                }
                if (actionStr.startsWith(value.getKeyword())) {
                    type = value;
                    break;
                }
            }
        }
        return type;
    }

    public static Permission GetPermission(Member member) {
        Permission memberPermission = Permission.MEMBER;
        if (member.getPermission() == MemberPermission.ADMINISTRATOR) {
            memberPermission = Permission.ADMIN;
        } else if (member.getPermission() == MemberPermission.OWNER) {
            memberPermission = Permission.OWNER;
        } else if (member.getSpecialTitle().contains("\uD83D\uDC51")) {
            memberPermission = Permission.CODING_EMPEROR;
        } else if (member.getSpecialTitle().contains("\uD83D\uDC2F")) {
            memberPermission = Permission.CODING_TIGER;
        }
        return memberPermission;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public List<AtTarget> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<AtTarget> targetList) {
        this.targetList = targetList;
    }

    public Permission getSenderPermission() {
        return senderPermission;
    }

    public void setSenderPermission(Permission senderPermission) {
        this.senderPermission = senderPermission;
    }

    public String getActionStr() {
        return actionStr;
    }

    public void setActionStr(String actionStr) {
        this.actionStr = actionStr;
    }

    public enum Permission {
        MEMBER(0),
        CODING_TIGER(1),
        CODING_EMPEROR(2),
        ADMIN(3),
        OWNER(4);

        final static Permission[] ALL_WELCOME = new Permission[]{Permission.OWNER, Permission.ADMIN, Permission.CODING_EMPEROR,
                Permission.CODING_TIGER, Permission.MEMBER};

        private int privilege;

        Permission(int privilege) {
            this.privilege = privilege;
        }

        public boolean lessThan(Permission permission) {
            return this.getPrivilege() < permission.getPrivilege();
        }

        public int getPrivilege() {
            return privilege;
        }

        public void setPrivilege(int privilege) {
            this.privilege = privilege;
        }
    }

}
