package com.raccoon.qqbot.db.dao;

import com.raccoon.qqbot.db.BotBaseMapper;
import com.raccoon.qqbot.db.entity.BotAdminActionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BotAdminActionDao extends BotBaseMapper<BotAdminActionEntity> {

    @Select("select * from bot_admin_action where admin_id = #{adminId} and member_id = #{memberId}" +
            " and status = #{status} order by id desc limit 1")
    BotAdminActionEntity selectByAdminUserStatus(@Param("adminId") long adminId, @Param("memberId") long memberId,
                                                 @Param("status") byte status);

    @Select("select * from bot_admin_action where member_id = #{memberId} and script_id = #{scriptId}" +
            " and status = #{status}")
    List<BotAdminActionEntity> selectByMemberScriptStatus(@Param("memberId") long memberId, @Param("scriptId") long scriptId,
                                                          @Param("status") byte status);


    @Select("select * from bot_admin_action where status = #{status}")
    List<BotAdminActionEntity> selectByStatus(@Param("status") byte status);

}
