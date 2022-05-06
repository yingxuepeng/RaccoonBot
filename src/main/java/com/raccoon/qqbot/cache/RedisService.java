package com.raccoon.qqbot.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisService {

    public static final int CACHE_TIME = 86400 * 1000;
    public static final String MSG_LIST = "MSG_";
    private SimpleDateFormat dateFormat;

    @PostConstruct
    public void init() {
        dateFormat = new SimpleDateFormat("YYYYMMdd");
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String getMsgListKey(long memberId, long timestamp) {
        return MSG_LIST + memberId + "_" + dateFormat.format(new Date(timestamp));
    }

    public List<Long> getMemberMsgTimeList(long memberId) {
        long ts = System.currentTimeMillis();
        List<String> msgTimeList = redisTemplate.opsForList().range(getMsgListKey(memberId, ts), 0, -1);
        List<Long> longList = msgTimeList.stream().map(Long::parseLong).collect(Collectors.toList());
        return longList;
    }

    public void putMsgTime(long memberId) {
        long ts = System.currentTimeMillis();
        String key = getMsgListKey(memberId, ts);
        redisTemplate.opsForList().leftPush(key, ts + "");
        redisTemplate.expire(key, CACHE_TIME, TimeUnit.MILLISECONDS);
    }

    public void clearMsgTimeList(long memberId) {
        long ts = System.currentTimeMillis();
        redisTemplate.delete(getMsgListKey(memberId, ts));
    }
}
