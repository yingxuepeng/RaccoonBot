package com.raccoon.qqbot.db.dao;

import com.raccoon.qqbot.db.BotBaseMapper;
import com.raccoon.qqbot.db.consts.BotGroupTopicConsts;
import com.raccoon.qqbot.db.entity.BotGroupTopicEntity;
import com.raccoon.qqbot.db.entity.BotMessageEntity;

public interface BotGroupTopicDao extends BotBaseMapper<BotGroupTopicEntity> {
    default BotGroupTopicEntity createEntity(BotMessageEntity messageEntity) {
        BotGroupTopicEntity entity = new BotGroupTopicEntity();
        entity.setGroupId(messageEntity.getGroupId());
        entity.setSenderId(messageEntity.getSenderId());
        String msg = messageEntity.getContent();
        msg = msg.substring(0, Math.min(128, msg.length()));
        entity.setTitle(msg);
        entity.setMsgStartId(messageEntity.getId());
        entity.setMsgEndId(null);
        entity.setMsgIgnoreIdsJson("[]");
        entity.setStatus(BotGroupTopicConsts.STATUS_CREATED);
        entity.setType(BotGroupTopicConsts.TYPE_COMMON);
        return entity;
    }
}
