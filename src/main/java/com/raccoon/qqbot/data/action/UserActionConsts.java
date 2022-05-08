package com.raccoon.qqbot.data.action;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;

public class UserActionConsts {
    public enum Permission {
        OWNER,
        ADMINISTRATOR,
        CODING_EMPEROR,
        MEMBER,
    }

    public enum Type {
        NONE(0, null),
        QUOTA_SHOW(1, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR}),
        QUOTA_INCREASE(2, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR}),
        QUOTA_DECREASE(3, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR}),
        QUOTA_ADDONELIFE(4, new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR}),
        MUTE_SELF(5, new Permission[]{Permission.MEMBER});

        private int type;
        private Permission[] permissionArray;

        Type(int type, Permission[] permissionArray) {
            this.type = type;
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

        public boolean hasPermission(Member member) {
            Permission memberPermission = Permission.MEMBER;
            if (member.getPermission() == MemberPermission.ADMINISTRATOR) {
                memberPermission = Permission.ADMINISTRATOR;
            } else if (member.getPermission() == MemberPermission.OWNER) {
                memberPermission = Permission.OWNER;
            } else if (member.getSpecialTitle().contains("\uD83D\uDC51")) {
                memberPermission = Permission.CODING_EMPEROR;
            }

            for (Permission permission : permissionArray) {
                if (permission == memberPermission) {
                    return true;
                }
            }
            return false;
        }
    }
}
