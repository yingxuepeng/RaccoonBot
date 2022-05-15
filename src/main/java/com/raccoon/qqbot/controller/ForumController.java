package com.raccoon.qqbot.controller;

import com.raccoon.qqbot.controller.data.TopicInfoResponse;
import com.raccoon.qqbot.controller.data.TopicKeyRequest;
import com.raccoon.qqbot.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = {"/api/"})
public class ForumController {

    @Autowired
    private TopicService topicService;

    @RequestMapping(value = "/topic/info", method = RequestMethod.GET)
    public TopicInfoResponse getTopicByKey(@Valid TopicKeyRequest request) {
        return topicService.getTopicByKey(request);
    }
}
