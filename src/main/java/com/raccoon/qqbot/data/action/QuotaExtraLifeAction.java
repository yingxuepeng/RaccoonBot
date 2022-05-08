package com.raccoon.qqbot.data.action;

public class QuotaExtraLifeAction extends UserAction {
    public int getMaxLifeCnt() {
        if (!getSenderPermission().lessThan(Permission.ADMINISTRATOR)) {
            return 3;
        } else if (getSenderPermission() == Permission.CODING_EMPEROR) {
            return 2;
        } else if (getSenderPermission() == Permission.CODING_TIGER) {
            return 1;
        }
        return 0;
    }

    public int getStep() {
        return 10;
    }
}
