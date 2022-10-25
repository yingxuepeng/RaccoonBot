package com.raccoon.qqbot.data.action;

import net.mamoe.mirai.contact.Member;

import static com.raccoon.qqbot.data.action.UserAction.Permission.ALL_WELCOME;

public enum ActionType {
    NONE(0, null, KeywordMatchType.NONE, null),
    QUOTA_SHOW(1, "看", KeywordMatchType.START_WITH, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN,
            UserAction.Permission.CODING_EMPEROR, UserAction.Permission.CODING_TIGER, UserAction.Permission.MEMBER}),
    QUOTA_INCREASE(2, "夸", KeywordMatchType.START_WITH, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN,
            UserAction.Permission.CODING_EMPEROR, UserAction.Permission.CODING_TIGER}),
    QUOTA_DECREASE(3, "干", KeywordMatchType.START_WITH, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN,
            UserAction.Permission.CODING_EMPEROR, UserAction.Permission.CODING_TIGER}),
    QUOTA_EXTRALIFE_ADD(4, "续", KeywordMatchType.START_WITH, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN,
            UserAction.Permission.CODING_EMPEROR, UserAction.Permission.CODING_TIGER}),

    MSG_ALL_TOP5(5, "matop5", KeywordMatchType.EQUAL, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN,
            UserAction.Permission.CODING_EMPEROR}),
    MSG_REPEAT_TOP5(6, "mrtop5", KeywordMatchType.EQUAL, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN,
            UserAction.Permission.CODING_EMPEROR}),
    // 禁言
    SMACK(7, "怼", KeywordMatchType.START_WITH, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN,
            UserAction.Permission.CODING_EMPEROR, UserAction.Permission.CODING_TIGER, UserAction.Permission.MEMBER}),

    TOPIC_CREATE(8, "mark", KeywordMatchType.EQUAL, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN, UserAction.Permission.CODING_EMPEROR}),
    TOPIC_LIST(9, "marklist", KeywordMatchType.EQUAL, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN, UserAction.Permission.CODING_EMPEROR,
            UserAction.Permission.CODING_TIGER, UserAction.Permission.MEMBER}),


    // holiday
    CONFIG_HOLIDAY(10, "过节", KeywordMatchType.START_WITH, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN}),
    CONFIG_WORK(11, "上班", KeywordMatchType.START_WITH, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN}),
    // 上票
    VOTE(12, "投", KeywordMatchType.START_WITH, ALL_WELCOME),
    // 处刑
    EXECUTION(13, "执行", KeywordMatchType.START_WITH, new UserAction.Permission[]{UserAction.Permission.OWNER, UserAction.Permission.ADMIN, UserAction.Permission.CODING_EMPEROR}),
    // 计算票数
    VOTE_COUNT(14, "计票", KeywordMatchType.START_WITH, ALL_WELCOME),
    ;

    private int type;
    private KeywordMatchType keywordMatchType;
    private UserAction.Permission[] permissionArray;
    private String keyword;

    ActionType(int type, String keyword, KeywordMatchType keywordMatchType, UserAction.Permission[] permissionArray) {
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

    public UserAction.Permission[] getPermissionArray() {
        return permissionArray;
    }

    public void setPermissionArray(UserAction.Permission[] permissionArray) {
        this.permissionArray = permissionArray;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public KeywordMatchType getKeywordMatchType() {
        return keywordMatchType;
    }

    public void setKeywordMatchType(KeywordMatchType keywordMatchType) {
        this.keywordMatchType = keywordMatchType;
    }

    public boolean hasPermission(Member member) {
        UserAction.Permission memberPermission = UserAction.GetPermission(member);
        for (UserAction.Permission permission : permissionArray) {
            if (permission == memberPermission) {
                return true;
            }
        }
        return false;
    }

    public static ActionType GetType(int type) {
        for (ActionType t : ActionType.values()) {
            if (t.getType() == type) {
                return t;
            }
        }
        return ActionType.NONE;
    }
}
