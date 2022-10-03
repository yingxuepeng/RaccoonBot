package com.raccoon.qqbot.data;

public class MsgConfig {
    private boolean isHoliday;
    private boolean isWeekend;

    public boolean getIsHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(boolean holiday) {
        isHoliday = holiday;
    }

    public boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(boolean weekend) {
        isWeekend = weekend;
    }
}
