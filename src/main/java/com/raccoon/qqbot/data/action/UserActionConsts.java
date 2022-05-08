package com.raccoon.qqbot.data.action;

import net.mamoe.mirai.contact.Member;

public class UserActionConsts {
    public enum Permission {
        MEMBER(0),
        CODING_EMPEROR(1),
        ADMINISTRATOR(2),
        OWNER(3);

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
        QUOTA_SHOW(1, "看", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR}),
        QUOTA_INCREASE(2, "夸", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR}),
        QUOTA_DECREASE(3, "干", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR, Permission.CODING_EMPEROR}),
        QUOTA_EXTRALIFE_ADD(4, "续", new Permission[]{Permission.OWNER, Permission.ADMINISTRATOR}),
        MUTE_SELF(5, "怼", new Permission[]{Permission.CODING_EMPEROR, Permission.MEMBER});

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
            Permission memberPermission = UserAction.GetPermission(member);
            for (Permission permission : permissionArray) {
                if (permission == memberPermission) {
                    return true;
                }
            }
            return false;
        }
    }
}
