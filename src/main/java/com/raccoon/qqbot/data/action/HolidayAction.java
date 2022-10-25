package com.raccoon.qqbot.data.action;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HolidayAction extends UserAction {
    private static Pattern holidayPattern = Pattern.compile("过节(\\d{1})天");
    private static Pattern workPattern = Pattern.compile("上班(\\d{1})天");

    public int getDays() {
        int days = 1;
        try {
            if (getType() == ActionType.CONFIG_HOLIDAY) {
                Matcher matcher = holidayPattern.matcher(getActionStr());
                if (matcher.find()) {
                    String dayStr = matcher.group(1);
                    days = Integer.parseInt(dayStr);
                }

            } else if (getType() == ActionType.CONFIG_WORK) {
                Matcher matcher = holidayPattern.matcher(getActionStr());
                if (matcher.find()) {
                    String dayStr = matcher.group(1);
                    days = Integer.parseInt(dayStr);
                }
            }

        } catch (Exception e) {
        }

        return days;
    }
}
