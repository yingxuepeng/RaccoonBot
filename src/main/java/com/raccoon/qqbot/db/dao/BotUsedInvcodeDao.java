package com.raccoon.qqbot.db.dao;

import com.raccoon.qqbot.db.BotBaseMapper;
import com.raccoon.qqbot.db.entity.BotUsedInvcodeEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface BotUsedInvcodeDao extends BotBaseMapper<BotUsedInvcodeEntity> {

    @Select("select * from bot_used_invcode where invcode = #{invcode} order by id desc limit 1")
    BotUsedInvcodeEntity selectByInvcode(@Param("invcode") String invcode);

    @Select("select * from bot_used_invcode where member_id = #{memberId} order by id desc limit 1")
    BotUsedInvcodeEntity selectByMemberId(@Param("memberId") Long memberId);
}
