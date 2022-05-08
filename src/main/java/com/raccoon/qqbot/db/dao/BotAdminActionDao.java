package com.raccoon.qqbot.db.dao;

import com.raccoon.qqbot.db.BotBaseMapper;
import com.raccoon.qqbot.db.consts.BotAdminActionConsts;
import com.raccoon.qqbot.db.entity.BotAdminActionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

public interface BotAdminActionDao extends BotBaseMapper<BotAdminActionEntity> {

    default BotAdminActionEntity createEntity(long adminId, long memberId, int type) {
        BotAdminActionEntity entity = new BotAdminActionEntity();
        entity.setAdminId(adminId);
        entity.setMemberId(memberId);
        entity.setScriptId(1L);
        entity.setQuotaCnt(0);
        entity.setQuotaStep(0);
        entity.setStatus(BotAdminActionConsts.STATUS_NORMAL);
        entity.setType((byte) type);
        entity.setIsDel(false);
        return entity;
    }

    @Select("select * from bot_admin_action where admin_id = #{adminId} and member_id = #{memberId}" +
            " and status = #{status} and type = #{type} order by id desc limit 1")
    BotAdminActionEntity selectByAdminMemberStatus(@Param("adminId") long adminId, @Param("memberId") long memberId,
                                                   @Param("status") byte status, @Param("type") byte type);

    @Select("select * from bot_admin_action where member_id = #{memberId} and script_id = #{scriptId}" +
            " and status = #{status}")
    List<BotAdminActionEntity> selectByMemberScriptStatus(@Param("memberId") long memberId, @Param("scriptId") long scriptId,
                                                          @Param("status") byte status);


    @Select("select * from bot_admin_action where status = #{status}")
    List<BotAdminActionEntity> selectByStatus(@Param("status") byte status);

}
