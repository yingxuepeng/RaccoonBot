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
 * @since 2022-08-06
 */
@TableName("bot_message")
public class BotMessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long senderId;

    private Long groupId;

    private Long msgId;

    private String senderNick;

    private String content;

    private String msgChainJstr;

    private Byte labelCrtype;

    private Integer labelType;

    private String labelFirst;

    private String labelSecond;

    private Float lfPby;

    private Float lsPby;

    private Timestamp createTime;

    private Boolean isDel;

    private Boolean isTrainable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }
    public String getSenderNick() {
        return senderNick;
    }

    public void setSenderNick(String senderNick) {
        this.senderNick = senderNick;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getMsgChainJstr() {
        return msgChainJstr;
    }

    public void setMsgChainJstr(String msgChainJstr) {
        this.msgChainJstr = msgChainJstr;
    }
    public Byte getLabelCrtype() {
        return labelCrtype;
    }

    public void setLabelCrtype(Byte labelCrtype) {
        this.labelCrtype = labelCrtype;
    }
    public Integer getLabelType() {
        return labelType;
    }

    public void setLabelType(Integer labelType) {
        this.labelType = labelType;
    }
    public String getLabelFirst() {
        return labelFirst;
    }

    public void setLabelFirst(String labelFirst) {
        this.labelFirst = labelFirst;
    }
    public String getLabelSecond() {
        return labelSecond;
    }

    public void setLabelSecond(String labelSecond) {
        this.labelSecond = labelSecond;
    }
    public Float getLfPby() {
        return lfPby;
    }

    public void setLfPby(Float lfPby) {
        this.lfPby = lfPby;
    }
    public Float getLsPby() {
        return lsPby;
    }

    public void setLsPby(Float lsPby) {
        this.lsPby = lsPby;
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
    public Boolean getIsTrainable() {
        return isTrainable;
    }

    public void setIsTrainable(Boolean isTrainable) {
        this.isTrainable = isTrainable;
    }

    @Override
    public String toString() {
        return "BotMessageEntity{" +
            "id=" + id +
            ", senderId=" + senderId +
            ", groupId=" + groupId +
            ", msgId=" + msgId +
            ", senderNick=" + senderNick +
            ", content=" + content +
            ", msgChainJstr=" + msgChainJstr +
            ", labelCrtype=" + labelCrtype +
            ", labelType=" + labelType +
            ", labelFirst=" + labelFirst +
            ", labelSecond=" + labelSecond +
            ", lfPby=" + lfPby +
            ", lsPby=" + lsPby +
            ", createTime=" + createTime +
            ", isDel=" + isDel +
            ", isTrainable=" + isTrainable +
        "}";
    }
}
