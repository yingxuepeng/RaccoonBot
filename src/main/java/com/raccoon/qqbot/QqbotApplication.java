package com.raccoon.qqbot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.raccoon.qqbot.db.dao")
@EnableScheduling
@EnableAsync
public class QqbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(QqbotApplication.class, args);
    }

}
