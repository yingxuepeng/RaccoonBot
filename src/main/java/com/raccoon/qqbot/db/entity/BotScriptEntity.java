package com.raccoon.qqbot.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.sql.Timestamp;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author pyx
 * @since 2022-05-15
 */
@TableName("bot_script")
public class BotScriptEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Byte type;

    private String scriptUrl;

    private String scriptEntrance;

    private Timestamp createTime;

    private Boolean isDel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
    public String getScriptUrl() {
        return scriptUrl;
    }

    public void setScriptUrl(String scriptUrl) {
        this.scriptUrl = scriptUrl;
    }
    public String getScriptEntrance() {
        return scriptEntrance;
    }

    public void setScriptEntrance(String scriptEntrance) {
        this.scriptEntrance = scriptEntrance;
    }
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    @Override
    public String toString() {
        return "BotScriptEntity{" +
            "id=" + id +
            ", type=" + type +
            ", scriptUrl=" + scriptUrl +
            ", scriptEntrance=" + scriptEntrance +
            ", createTime=" + createTime +
            ", isDel=" + isDel +
        "}";
    }
}
