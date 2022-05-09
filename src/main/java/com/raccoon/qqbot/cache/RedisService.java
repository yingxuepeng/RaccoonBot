package com.raccoon.qqbot.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    public static final int CACHE_TIME = 86400 * 1000 * 30;
    public static final String MSGLIST_KEY = "MSG_LRU";
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
//    public List<Long> getMemberMsgTimeList(long memberId) {
//        long ts = System.currentTimeMillis();
//        List<String> msgTimeList = redisTemplate.opsForList().range(getMsgListKey(memberId, ts), 0, -1);
//        List<Long> longList = msgTimeList.stream().map(Long::parseLong).collect(Collectors.toList());
//        return longList;
//    }

    public void putMsgTime(long memberId) {
        long ts = System.currentTimeMillis();
        String key = getMsgListKey(memberId, ts);
        redisTemplate.opsForList().leftPush(key, ts + "");
        redisTemplate.expire(key, CACHE_TIME, TimeUnit.MILLISECONDS);
    }
}
