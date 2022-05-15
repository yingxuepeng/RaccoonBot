package com.raccoon.qqbot.controller.data;

import com.raccoon.qqbot.db.entity.BotGroupTopicEntity;
import com.raccoon.qqbot.db.entity.BotMessageEntity;

import java.util.List;

public class TopicInfoResponse {
    BotGroupTopicEntity topic;
    List<BotMessageEntity> msgList;

    public BotGroupTopicEntity getTopic() {
        return topic;
    }

    public void setTopic(BotGroupTopicEntity topic) {
        this.topic = topic;
    }

    public List<BotMessageEntity> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<BotMessageEntity> msgList) {
        this.msgList = msgList;
    }
}
