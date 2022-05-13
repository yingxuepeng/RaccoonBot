package com.raccoon.qqbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raccoon.qqbot.cache.RedisService;
import com.raccoon.qqbot.config.MiraiConfig;
import com.raccoon.qqbot.db.dao.*;
import com.raccoon.qqbot.service.external.QCloudService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class BaseService {
    // json
    protected ObjectMapper objectMapper;

    // mirai data
    @Autowired
    protected Bot miraiBot;
    @Autowired
    protected MiraiConfig.MiraiInfo miraiInfo;

    // external service
    @Autowired
    protected RedisService redisService;
    @Autowired
    protected QCloudService qCloudService;
    // dao
    @Resource
    protected SolutionDao solutionDao;
    @Resource
    protected BotAdminActionDao botAdminActionDao;
    @Resource
    protected BotUsedInvcodeDao botUsedInvcodeDao;
    @Resource
    protected BotScriptDao botScriptDao;
    @Resource
    protected BotMessageDao botMessageDao;
    @Resource
    protected BotGroupTopicDao botGroupTopicDao;


    @PostConstruct
    protected void init() {
        objectMapper = new ObjectMapper();
    }


    public void sendNoPermissionMessage(Group group) {
        group.sendMessage("~权限不足，小浣熊哭哭~");
    }

    protected LocalDateTime getTodayMidnight() {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        return todayMidnight;

    }
}
