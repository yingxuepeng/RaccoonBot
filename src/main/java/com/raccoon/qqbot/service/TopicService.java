package com.raccoon.qqbot.service;

import com.raccoon.qqbot.controller.data.TopicInfoResponse;
import com.raccoon.qqbot.controller.data.TopicKeyRequest;
import com.raccoon.qqbot.data.action.UserAction;
import com.raccoon.qqbot.db.dao.BotGroupTopicDao;
import com.raccoon.qqbot.db.dao.BotMessageDao;
import com.raccoon.qqbot.db.entity.BotGroupTopicEntity;
import com.raccoon.qqbot.db.entity.BotMessageEntity;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    @Autowired
    private BotGroupTopicDao botGroupTopicDao;
    @Autowired
    private BotMessageDao botMessageDao;

    public TopicInfoResponse getTopicByKey(TopicKeyRequest request) {
        TopicInfoResponse resp = new TopicInfoResponse();

        BotGroupTopicEntity topicEntity = botGroupTopicDao.getTopicByKey(request.getTopicKey());
        if (topicEntity == null) {
            return resp;
        }

        resp.setTopic(topicEntity);
        List<BotMessageEntity> msgList = botMessageDao.selectTopicMsgList(topicEntity.getMsgStartId(), 50);
        resp.setMsgList(msgList);

        return resp;
    }

    public void sendTopicList(GroupMessageEvent event, UserAction action) {
        final int TOPIC_CNT = 10;
        List<BotGroupTopicEntity> topicList = botGroupTopicDao.getTopicLatestList(TOPIC_CNT);
        String msg = "最近" + TOPIC_CNT + "条mark消息：\n";
        for (BotGroupTopicEntity topic : topicList) {
            msg += "主题：" + StringUtils.left(topic.getTitle(), 32) + "\n";
            msg += "https://forum.primeoj.com/web/topic/info?topicKey=" + topic.getTopicKey() + "\n";
        }
        event.getGroup().sendMessage(msg);
    }
}
