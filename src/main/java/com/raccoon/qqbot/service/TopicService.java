package com.raccoon.qqbot.service;

import com.raccoon.qqbot.controller.data.TopicInfoResponse;
import com.raccoon.qqbot.controller.data.TopicKeyRequest;
import com.raccoon.qqbot.db.dao.BotGroupTopicDao;
import com.raccoon.qqbot.db.dao.BotMessageDao;
import com.raccoon.qqbot.db.entity.BotGroupTopicEntity;
import com.raccoon.qqbot.db.entity.BotMessageEntity;
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
}
