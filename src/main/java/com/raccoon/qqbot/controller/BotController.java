package com.raccoon.qqbot.controller;

import com.raccoon.qqbot.config.MiraiConfig;
import com.raccoon.qqbot.data.action.UserActionVo;
import com.raccoon.qqbot.service.BotService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class BotController {

    // mirai data
    @Autowired
    private Bot miraiBot;


    @Autowired
    private MiraiConfig.MiraiInfo miraiInfo;

    // service
    @Autowired
    private BotService botService;


    @PostConstruct
    public void init() {
        // 上线
        miraiBot.getEventChannel().subscribeAlways(BotOnlineEvent.class, event -> {

        });
        // 掉线
        miraiBot.getEventChannel().subscribeAlways(BotOfflineEvent.class, event -> {
        });

        // 申请加群
        miraiBot.getEventChannel().subscribeAlways(MemberJoinRequestEvent.class, event -> {
            if (event.getGroupId() != miraiInfo.getGroupId()) {
                return;
            }
            botService.handleJoinRequest(event);
        });

        // 用户加群
        miraiBot.getEventChannel().subscribeAlways(MemberJoinEvent.class, event -> {
            if (event.getGroupId() != miraiInfo.getGroupId()) {
                return;
            }
            botService.sendWelcomeMessage(event);
        });

        // 收到群聊消息
        miraiBot.getEventChannel().subscribeAlways(GroupMessageEvent.class, event -> {
            // 非管理组
            if (event.getGroup().getId() != miraiInfo.getGroupId()) {
                return;
            }
            String title = event.getSender().getSpecialTitle();
            System.out.println(title);
//            if (event.getMessage())
            // 获取action
            UserActionVo userActionVo = UserActionVo.From(event, miraiInfo);
            if (userActionVo == null) {
                botService.saveMemberMsg(event);
                return;
            }
            // 权限判断
            if (!userActionVo.getType().hasPermission(event.getSender())) {
                sendNoPermissionMessage(event.getGroup());
                return;
            }

            // 根据type调用不同service func
            switch (userActionVo.getType()) {
                case QUOTA_SHOW:
                    break;
                case QUOTA_INCREASE:
                    break;
                case QUOTA_DECREASE:
                    break;
                default:
                    break;

            }
        });


        // 收到单聊消息
        miraiBot.getEventChannel().subscribeAlways(FriendMessageEvent.class, event -> {
            botService.showMemberQuota(event);
//            String s = event.getMessage().contentToString();
//            if (s.contains("\uD83D\uDC51"))
//                System.out.println(s);
        });

        // 收到陌生人单聊消息
        miraiBot.getEventChannel().subscribeAlways(StrangerMessageEvent.class, event -> {
            botService.showMyQuota(event);
        });

        miraiBot.login();
    }


    private void sendNoPermissionMessage(Group group) {
        group.sendMessage("~权限不足，小浣熊哭哭~");
    }
}
