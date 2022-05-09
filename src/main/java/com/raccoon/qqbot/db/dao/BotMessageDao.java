package com.raccoon.qqbot.db.dao;

import com.raccoon.qqbot.data.BotUserQuotaVo;
import com.raccoon.qqbot.db.BotBaseMapper;
import com.raccoon.qqbot.db.entity.BotMessageEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface BotMessageDao extends BotBaseMapper<BotMessageEntity> {
    @Select("select label_type,label_first,label_second from bot_message where sender_id = #{senderId} and create_time >= #{createTime}")
    List<BotMessageEntity> selectMessageBrief(@Param("senderId") long senderId, @Param("createTime") LocalDateTime createTime);

    /**
     * @return [{senderId, msgCnt}, ...]
     */
    @Select("select sender_id,count(1) as msg_cnt from bot_message where create_time >= #{createTime} group by sender_id order by count(1) desc limit 5")
    List<BotUserQuotaVo> selectDailyTop5(@Param("createTime") LocalDateTime createTime);
}
