package com.raccoon.qqbot.data.action;

import com.raccoon.qqbot.config.MiraiConfig;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.PlainText;

public class UserAction {
    private Type type;
    private long senderId;
    private long targetId;

    private Permission senderPermission;
    private Permission targetPermission;
    private String actionStr;


    public static UserAction From(GroupMessageEvent event, MiraiConfig.MiraiInfo miraiInfo) {

        if (event.getMessage().size() < 3) {
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

        // get action type
        Type type = Type.NONE;
        String actionStr = action.getContent().trim();
        for (Type value : Type.values()) {
            if (value.getKeyword() == null) {
                continue;
            }
            if (actionStr.startsWith(value.getKeyword())) {
                type = value;
                break;
            }
        }

        if (type == Type.NONE) {
            return null;
        }

        UserAction userAction = null;
        switch (type) {
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
            default:
                userAction = new UserAction();
                break;
        }
        userAction.type = type;
        userAction.senderId = event.getSender().getId();
        userAction.targetId = targetId;
        userAction.senderPermission = GetPermission(event.getSender());
        userAction.targetPermission = GetPermission(event.getGroup().get(targetId));
        userAction.actionStr = actionStr;

        return userAction;
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
        NONE(0, null, null),
        QUOTA_SHOW(1, "看", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR, Permission.CODING_TIGER, Permission.MEMBER}),
        QUOTA_INCREASE(2, "夸", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR, Permission.CODING_TIGER}),
        QUOTA_DECREASE(3, "干", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR, Permission.CODING_TIGER}),
        QUOTA_EXTRALIFE_ADD(4, "续", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR, Permission.CODING_TIGER}),

        MSG_TOP5(5, "TOP5", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR}),
        // 禁言自己
        MUTE_SELF(6, "怼", new Permission[]{Permission.CODING_EMPEROR, Permission.CODING_TIGER, Permission.MEMBER});

        private int type;
        private Permission[] permissionArray;
        private String keyword;

        Type(int type, String keyword, Permission[] permissionArray) {
            this.type = type;
            this.permissionArray = permissionArray;
            this.keyword = keyword;
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
}
