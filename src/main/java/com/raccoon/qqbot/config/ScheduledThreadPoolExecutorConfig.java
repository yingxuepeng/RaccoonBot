package com.raccoon.qqbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
public class ScheduledThreadPoolExecutorConfig {

    // todo 应该定义配置
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);

    @Bean
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor(){
        return this.scheduledThreadPoolExecutor;
    }
}
