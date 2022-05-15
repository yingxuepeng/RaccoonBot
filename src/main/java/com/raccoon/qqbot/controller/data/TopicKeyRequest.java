package com.raccoon.qqbot.controller.data;

import javax.validation.constraints.NotNull;

public class TopicKeyRequest {
    @NotNull
    private String topicKey;

    public String getTopicKey() {
        return topicKey;
    }

    public void setTopicKey(String topicKey) {
        this.topicKey = topicKey;
    }
}
