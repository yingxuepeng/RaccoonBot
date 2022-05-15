package com.raccoon.qqbot.db.dao;

import com.raccoon.qqbot.db.BotBaseMapper;
import com.raccoon.qqbot.db.consts.BotGroupTopicConsts;
import com.raccoon.qqbot.db.entity.BotGroupTopicEntity;
import com.raccoon.qqbot.db.entity.BotMessageEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BotGroupTopicDao extends BotBaseMapper<BotGroupTopicEntity> {
    default BotGroupTopicEntity createEntity(BotMessageEntity messageEntity) {
        BotGroupTopicEntity entity = new BotGroupTopicEntity();
        entity.setGroupId(messageEntity.getGroupId());
        entity.setSenderId(messageEntity.getSenderId());
        String msg = messageEntity.getContent();
        msg = StringUtils.left(msg, 200);
        entity.setTitle(msg);
        entity.setMsgStartId(messageEntity.getId());
        entity.setMsgEndId(null);
        entity.setMsgIgnoreIdsJson("[]");
        entity.setStatus(BotGroupTopicConsts.STATUS_CREATED);
        entity.setType(BotGroupTopicConsts.TYPE_COMMON);
        // topic key
        entity.setTopicKey(topicKeyGenerator());
        return entity;
    }

    default String topicKeyGenerator() {
        String key = "";
        // 用字符数组的方式随机
        final String model = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0";
        char[] m = model.toCharArray();
        for (int j = 0; j < 32; j++) {
            char c = m[(int) (Math.random() * 36)];
            key = key + c;
        }
        return key;
    }


    @Select("select * from bot_group_topic order by id desc limit #{limit}")
    List<BotGroupTopicEntity> getTopicLatestList(@Param("limit") int limit);

    @Select("select * from bot_group_topic where topic_key = #{topicKey} limit 1")
    BotGroupTopicEntity getTopicByKey(@Param("topicKey") String topicKey);
}
