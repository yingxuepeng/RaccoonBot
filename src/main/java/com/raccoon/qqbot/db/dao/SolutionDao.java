package com.raccoon.qqbot.db.dao;

import com.raccoon.qqbot.db.BotBaseMapper;
import com.raccoon.qqbot.db.entity.SolutionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionDao extends BotBaseMapper<SolutionEntity> {
    @Select("select * from solution where solution_uuid = #{uuid} order by solution_id desc limit 1")
    SolutionEntity getByUuid(@Param("uuid") String uuid);
}
