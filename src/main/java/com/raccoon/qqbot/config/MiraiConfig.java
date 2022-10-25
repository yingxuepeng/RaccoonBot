package com.raccoon.qqbot.config;

import com.raccoon.qqbot.data.action.ActionType;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MiraiConfig {
    @Value("${com.raccoon.qqbot.mirai.qid}")
    private String loginQid;
    @Value("${com.raccoon.qqbot.mirai.pwdmd5}")
    private String loginPwdMd5;
    @Value("${com.raccoon.qqbot.mirai.groupid}")
    private String groupId;

    @Value("${com.raccoon.qqbot.valid-action-type-list}")
    private String validActionTypeList;

    @Bean("miraiBot")
    public Bot getMiraiBot() {

        Bot miraiBot = BotFactory.INSTANCE.newBot(Long.parseLong(loginQid), HexStringToByteArray(loginPwdMd5), botConfiguration -> {
            botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
            botConfiguration.setAutoReconnectOnForceOffline(true);
            botConfiguration.setReconnectionRetryTimes(Integer.MAX_VALUE);
        });

        return miraiBot;
    }

    @Bean("miraiInfo")
    public MiraiInfo getMiraiInfo() {
        MiraiInfo miraiInfo = new MiraiInfo();
        miraiInfo.setBotId(Long.parseLong(loginQid));
        miraiInfo.setGroupId(Long.parseLong(groupId));
        // set valid action type list
        List<ActionType> typeList = new ArrayList<>();
        String[] typePeroidStr = validActionTypeList.split(",");
        for (String peroidStr : typePeroidStr) {
            String[] rangeStr = peroidStr.trim().split("-");
            try {
                if (rangeStr.length == 1) {
                    // ...,5,...
                    int type = Integer.parseInt(rangeStr[0].trim());
                    typeList.add(ActionType.GetType(type));
                } else if (rangeStr.length == 2) {
                    // ...,1-10,...
                    int beginType = Integer.parseInt(rangeStr[0].trim());
                    int endType = Integer.parseInt(rangeStr[1].trim());
                    for (int type = beginType; type <= endType; type++) {
                        typeList.add(ActionType.GetType(type));
                    }
                }
            } catch (NumberFormatException e) {

            }
        }
        miraiInfo.setValideActionTypeList(typeList);
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

        private List<ActionType> valideActionTypeList;

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

        public List<ActionType> getValideActionTypeList() {
            return valideActionTypeList;
        }

        public void setValideActionTypeList(List<ActionType> valideActionTypeList) {
            this.valideActionTypeList = valideActionTypeList;
        }
    }
}
