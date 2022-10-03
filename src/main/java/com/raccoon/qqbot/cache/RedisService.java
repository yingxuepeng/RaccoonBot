package com.raccoon.qqbot.cache;

import com.raccoon.qqbot.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    public static final String MSGLIST_KEY = "MSG_LRU";

    public static final String ISHOLIDAY_KEY = "ISHOLIDAY";
    private SimpleDateFormat dateFormat;

    @PostConstruct
    public void init() {
        dateFormat = new SimpleDateFormat("YYYYMMdd");
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String getMsgListKey(long memberId, long timestamp) {
        return MSGLIST_KEY + memberId + "_" + dateFormat.format(new Date(timestamp));
    }


    public boolean hasMsg(String msg) {
        Long rank = redisTemplate.opsForZSet().rank(MSGLIST_KEY, msg);
        return rank != null;
    }

    public void putMsgKey(String msg, int limit) {
        int score = (int) (System.currentTimeMillis() / 1000);
        redisTemplate.opsForZSet().add(MSGLIST_KEY, msg, score);
        Long cnt = redisTemplate.opsForZSet().zCard(MSGLIST_KEY);
        if (cnt == null) {
            return;
        }
        if (cnt > limit) {
            redisTemplate.opsForZSet().popMin(MSGLIST_KEY, cnt - limit);
        }
    }

    public void setIsHoliday(boolean isHoliday, int days) {
        long remainSecond = TimeUtils.GetTodayRemainSecond() + (days - 1) * 86400;
        redisTemplate.opsForValue().set(ISHOLIDAY_KEY, isHoliday + "", remainSecond, TimeUnit.SECONDS);
    }

    public Boolean getIsHoliday() {
        String isHolidayStr = redisTemplate.opsForValue().get(ISHOLIDAY_KEY);
        if (isHolidayStr == null) {
            return null;
        }
        return Boolean.parseBoolean(isHolidayStr);
    }
}
