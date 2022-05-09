package com.raccoon.qqbot.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author pyx
 * @since 2022-05-09
 */
@TableName("bot_admin_action")
public class BotAdminActionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private Long memberId;

    private Long scriptId;

    private Byte status;

    private Byte type;

    private Integer quotaCnt;

    private Integer quotaStep;

    private LocalDateTime createTime;

    private LocalDateTime expireTime;

    private Boolean isDel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
    }
    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
    public Integer getQuotaCnt() {
        return quotaCnt;
    }

    public void setQuotaCnt(Integer quotaCnt) {
        this.quotaCnt = quotaCnt;
    }
    public Integer getQuotaStep() {
        return quotaStep;
    }

    public void setQuotaStep(Integer quotaStep) {
        this.quotaStep = quotaStep;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    @Override
    public String toString() {
        return "BotAdminActionEntity{" +
            "id=" + id +
            ", adminId=" + adminId +
            ", memberId=" + memberId +
            ", scriptId=" + scriptId +
            ", status=" + status +
            ", type=" + type +
            ", quotaCnt=" + quotaCnt +
            ", quotaStep=" + quotaStep +
            ", createTime=" + createTime +
            ", expireTime=" + expireTime +
            ", isDel=" + isDel +
        "}";
    }
}
