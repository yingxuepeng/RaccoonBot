package com.raccoon.qqbot.config;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MiraiConfig {
    @Value("${com.raccoon.qqbot.mirai.qid}")
    private String loginQid;
    @Value("${com.raccoon.qqbot.mirai.pwdmd5}")
    private String loginPwdMd5;
    @Value("${com.raccoon.qqbot.mirai.groupid}")
    private String groupId;

    @Bean("miraiBot")
    public Bot getMiraiBot() {

        Bot miraiBot = BotFactory.INSTANCE.newBot(Long.parseLong(loginQid), HexStringToByteArray(loginPwdMd5), botConfiguration -> {
        });
        return miraiBot;
    }

    @Bean("miraiInfo")
    public MiraiInfo getMiraiInfo() {
        MiraiInfo miraiInfo = new MiraiInfo();
        miraiInfo.setBotId(Long.parseLong(loginQid));
        miraiInfo.setGroupId(Long.parseLong(groupId));
        return miraiInfo;
    }

    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static final class MiraiInfo {
        private Long botId;
        private Long groupId;

        public Long getBotId() {
            return botId;
        }

        public void setBotId(Long botId) {
            this.botId = botId;
        }

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

    }
}
