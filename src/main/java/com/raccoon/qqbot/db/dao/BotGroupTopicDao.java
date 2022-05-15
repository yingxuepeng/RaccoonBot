package com.raccoon.qqbot.db.dao;

import com.raccoon.qqbot.db.BotBaseMapper;
import com.raccoon.qqbot.db.consts.BotGroupTopicConsts;
import com.raccoon.qqbot.db.entity.BotGroupTopicEntity;
import com.raccoon.qqbot.db.entity.BotMessageEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    @Select("select * from bot_group_topic order by id desc limit #{limit}")
    List<BotGroupTopicEntity> getTopicLatestList(@Param("limit") int limit);

    @Select("select * from bot_group_topic where topic_key = #{topicKey} limit 1")
    BotGroupTopicEntity getTopicByKey(@Param("topicKey") String topicKey);
}
