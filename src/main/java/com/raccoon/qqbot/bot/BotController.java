package com.raccoon.qqbot.bot;

import com.raccoon.qqbot.config.MiraiConfig;
import com.raccoon.qqbot.data.action.*;
import com.raccoon.qqbot.service.GroupJoinService;
import com.raccoon.qqbot.service.GroupMsgService;
import com.raccoon.qqbot.service.TopicService;
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
    private GroupMsgService groupMsgService;
    @Autowired
    private GroupJoinService groupJoinService;
    @Autowired
    private TopicService topicService;


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
            groupJoinService.handleJoinRequest(event);
        });

        // 用户加群
        miraiBot.getEventChannel().subscribeAlways(MemberJoinEvent.class, event -> {
            if (event.getGroupId() != miraiInfo.getGroupId()) {
                return;
            }
            groupJoinService.sendWelcomeMessage(event);
        });
        // 收到群聊消息
        miraiBot.getEventChannel().subscribeAlways(GroupMessageEvent.class, event -> {
            // 非管理群组
            if (event.getGroup().getId() != miraiInfo.getGroupId()) {
                return;
            }
            // 获取action
            UserAction userAction = UserAction.from(event, miraiInfo);

            if (userAction == null) {
                groupMsgService.saveMsg(event);
                groupMsgService.checkQuota(event);
                return;
            }
            // 权限判断
            if (!userAction.getType().hasPermission(event.getSender())) {
                groupMsgService.sendNoPermissionMessage(event.getGroup());
                return;
            }

            // 根据type调用不同service func
            switch (userAction.getType()) {
                case QUOTA_SHOW:
                    groupMsgService.showQuota(event, (QuotaShowAction) userAction);
                    break;
                case QUOTA_INCREASE:
                case QUOTA_DECREASE:
                    groupMsgService.changeQuota(event, (QuotaChangeAction) userAction);
                    break;
                case QUOTA_EXTRALIFE_ADD:
                    groupMsgService.addExtraLife(event, (QuotaExtraLifeAction) userAction);
                    break;
                case MSG_ALL_TOP5:
                    groupMsgService.showMsgAllTop5(event);
                    break;
                case MSG_REPEAT_TOP5:
                    groupMsgService.showMsgRepeatTop5(event);
                    break;
                case TOPIC_CREATE:
                    groupMsgService.createTopic(event, (QuoteAction) userAction);
                    break;
                case TOPIC_LIST:
                    topicService.sendTopicList(event, userAction);
                    break;
                case VOTE:
                    groupMsgService.vote(event, userAction);
                    break;
                case EXECUTION:
                    groupMsgService.execute(event, userAction);
                    break;
                case VOTE_COUNT:
                    groupMsgService.showVotes(event, userAction);
                    break;
                case CONFIG_HOLIDAY:
                case CONFIG_WORK:
                    groupMsgService.setIsHoliday(event, userAction);
                default:
                    break;
            }
        });

        // 收到单聊消息
        miraiBot.getEventChannel().subscribeAlways(FriendMessageEvent.class, event -> {
            groupMsgService.showMemberQuota(event);
//            botService.showQuotaRank(null);
        });

        // 收到陌生人单聊消息
        miraiBot.getEventChannel().subscribeAlways(StrangerMessageEvent.class, event -> {
            groupMsgService.showMyQuota(event);
        });

        miraiBot.login();
    }
}
