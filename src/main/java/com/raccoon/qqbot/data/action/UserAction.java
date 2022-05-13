package com.raccoon.qqbot.data.action;

import com.raccoon.qqbot.config.MiraiConfig;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;

public class UserAction {
    private Type type;
    private long senderId;
    private long targetId;

    private Permission senderPermission;
    private Permission targetPermission;
    private String actionStr;


    public static UserAction From(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {
        if (event.getMessage().size() < 2) {
            return null;
        }
        if ((event.getMessage().get(1) instanceof At)) {
            return getAtAction(event, miraiInfo);
        } else if ((event.getMessage().get(1) instanceof QuoteReply)) {
            return getQuoteAction(event, miraiInfo);
        }
        return null;
    }

    public static UserAction getAtAction(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {
        if (event.getMessage().size() < 3) {
            return null;
        }
        // first is bot
        At me = (At) event.getMessage().get(1);
        if (me.getTarget() != miraiInfo.getBotId()) {
            return null;
        }
        // third is target
        Long targetId = null;

        if (event.getMessage().size() > 3 && (event.getMessage().get(3) instanceof At)) {
            // 未指定，则为自己
            At target = (At) event.getMessage().get(3);
            targetId = target.getTarget();
        } else {
            targetId = event.getSender().getId();
        }

        // action
        if (!(event.getMessage().get(2) instanceof PlainText)) {
            return null;
        }
        PlainText action = (PlainText) event.getMessage().get(2);
        String actionStr = action.getContent().trim().toLowerCase();

        Type type = getType(actionStr);
        // user action
        UserAction userAction = null;
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
            case MSG_ALL_TOP5:
            case MSG_REPEAT_TOP5:
                // 禁言自己
            case MUTE_SELF:
                break;


            case TOPIC_CREATE:
                userAction = new QuoteAction();
                break;
            case TOPIC_END:
            default:
                break;
        }
        if (userAction != null) {
            userAction.type = type;
            userAction.senderId = event.getSender().getId();
            userAction.targetId = targetId;
            userAction.senderPermission = GetPermission(event.getSender());
            userAction.targetPermission = GetPermission(event.getGroup().get(targetId));
            userAction.actionStr = actionStr;
        }


        return userAction;
    }

    public static UserAction getQuoteAction(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {
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
        Type type = getType(actionStr);

        long targetId = quoteReply.getSource().getIds()[0];
        // user action
        QuoteAction quoteAction = null;
        switch (type) {

            case TOPIC_CREATE:
                quoteAction = new QuoteAction();
                break;
            case TOPIC_END:
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

    public static Type getType(String actionStr) {
        // get action type
        Type type = Type.NONE;
        //  equal first
        for (Type value : Type.values()) {
            if (value.keywordMatchType != KeywordMatchType.EQUAL) {
                continue;
            }
            if (actionStr.equals(value.getKeyword())) {
                type = value;
                break;
            }
        }
        // is not then  start with
        if (type == Type.NONE) {
            for (Type value : Type.values()) {
                if (value.keywordMatchType != KeywordMatchType.START_WITH) {
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
            memberPermission = Permission.ADMINISTRATOR;
        } else if (member.getPermission() == MemberPermission.OWNER) {
            memberPermission = Permission.OWNER;
        } else if (member.getSpecialTitle().contains("\uD83D\uDC51")) {
            memberPermission = Permission.CODING_EMPEROR;
        } else if (member.getSpecialTitle().contains("\uD83D\uDC2F")) {
            memberPermission = Permission.CODING_TIGER;
        }
        return memberPermission;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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

    public Permission getSenderPermission() {
        return senderPermission;
    }

    public void setSenderPermission(Permission senderPermission) {
        this.senderPermission = senderPermission;
    }

    public Permission getTargetPermission() {
        return targetPermission;
    }

    public void setTargetPermission(Permission targetPermission) {
        this.targetPermission = targetPermission;
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
        ADMINISTRATOR(3),
        OWNER(4);

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

    public enum Type {
        NONE(0, null, KeywordMatchType.NONE, null),
        QUOTA_SHOW(1, "看", KeywordMatchType.START_WITH, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR,
                Permission.CODING_EMPEROR, Permission.CODING_TIGER, Permission.MEMBER}),
        QUOTA_INCREASE(2, "夸", KeywordMatchType.START_WITH, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR,
                Permission.CODING_EMPEROR, Permission.CODING_TIGER}),
        QUOTA_DECREASE(3, "干", KeywordMatchType.START_WITH, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR,
                Permission.CODING_EMPEROR, Permission.CODING_TIGER}),
        QUOTA_EXTRALIFE_ADD(4, "续", KeywordMatchType.START_WITH, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR,
                Permission.CODING_EMPEROR, Permission.CODING_TIGER}),

        MSG_ALL_TOP5(5, "matop5", KeywordMatchType.EQUAL, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR,
                Permission.CODING_EMPEROR}),
        MSG_REPEAT_TOP5(6, "mrtop5", KeywordMatchType.EQUAL, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR,
                Permission.CODING_EMPEROR}),
        // 禁言自己
        MUTE_SELF(7, "怼我", KeywordMatchType.EQUAL, new Permission[]{Permission.CODING_EMPEROR, Permission.CODING_TIGER, Permission.MEMBER}),

        TOPIC_CREATE(8, "mark", KeywordMatchType.EQUAL, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR}),
        TOPIC_END(9, "marke", KeywordMatchType.EQUAL, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR});

        private int type;
        private KeywordMatchType keywordMatchType;
        private Permission[] permissionArray;
        private String keyword;

        Type(int type, String keyword, KeywordMatchType keywordMatchType, Permission[] permissionArray) {
            this.type = type;
            this.keyword = keyword;
            this.keywordMatchType = keywordMatchType;
            this.permissionArray = permissionArray;

        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Permission[] getPermissionArray() {
            return permissionArray;
        }

        public void setPermissionArray(Permission[] permissionArray) {
            this.permissionArray = permissionArray;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public boolean hasPermission(Member member) {
            Permission memberPermission = GetPermission(member);
            for (Permission permission : permissionArray) {
                if (permission == memberPermission) {
                    return true;
                }
            }
            return false;
        }
    }

    private enum KeywordMatchType {
        NONE,
        EQUAL,
        START_WITH,
        REGEX,
    }
}
