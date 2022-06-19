package com.raccoon.qqbot.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;

public class TimeUtils {

    public static long GetTodayRemainSecond() {
        ZonedDateTime nowZoned = ZonedDateTime.now();
        Instant midnight = nowZoned.toLocalDate().atStartOfDay(nowZoned.getZone()).toInstant();
        Duration duration = Duration.between(midnight, Instant.now());
        return 86400 - duration.getSeconds();
    }
}
