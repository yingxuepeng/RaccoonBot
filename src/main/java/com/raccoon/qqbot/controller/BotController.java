package com.raccoon.qqbot.controller;

import com.raccoon.qqbot.config.MiraiConfig;
import com.raccoon.qqbot.data.action.QuotaChangeAction;
import com.raccoon.qqbot.data.action.QuotaExtraLifeAction;
import com.raccoon.qqbot.data.action.QuotaShowAction;
import com.raccoon.qqbot.data.action.UserAction;
import com.raccoon.qqbot.service.BotService;
import net.mamoe.mirai.Bot;
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
            // 获取action
            UserAction userAction = UserAction.From(event, miraiInfo);
            if (userAction == null) {
                botService.saveMemberMsg(event);
                botService.handleMemberMsg(event);
                return;
            }
            // 权限判断
            if (!userAction.getType().hasPermission(event.getSender())) {
                botService.sendNoPermissionMessage(event.getGroup());
                return;
            }

            // 根据type调用不同service func
            switch (userAction.getType()) {
                case QUOTA_SHOW:
                    botService.showQuota(event, (QuotaShowAction) userAction);
                    break;
                case QUOTA_INCREASE:
                case QUOTA_DECREASE:
                    botService.changeQuota(event, (QuotaChangeAction) userAction);
                    break;
                case QUOTA_EXTRALIFE_ADD:
                    botService.addExtraLife(event, (QuotaExtraLifeAction) userAction);
                default:
                    break;
            }
        });

        // 收到单聊消息
        miraiBot.getEventChannel().subscribeAlways(FriendMessageEvent.class, event -> {
            botService.showMemberQuota(event);
        });

        // 收到陌生人单聊消息
        miraiBot.getEventChannel().subscribeAlways(StrangerMessageEvent.class, event -> {
            botService.showMyQuota(event);
        });

        miraiBot.login();
    }
}
