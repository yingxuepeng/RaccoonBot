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
@TableName("bot_group_topic")
public class BotGroupTopicEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long groupId;

    private Long senderId;

    private Long msgStartId;

    private Long msgEndId;

    private String msgIgnoreIdsJson;

    private String topicKey;

    private Byte status;

    private Integer type;

    private String title;

    private Timestamp createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public Long getMsgStartId() {
        return msgStartId;
    }

    public void setMsgStartId(Long msgStartId) {
        this.msgStartId = msgStartId;
    }
    public Long getMsgEndId() {
        return msgEndId;
    }

    public void setMsgEndId(Long msgEndId) {
        this.msgEndId = msgEndId;
    }
    public String getMsgIgnoreIdsJson() {
        return msgIgnoreIdsJson;
    }

    public void setMsgIgnoreIdsJson(String msgIgnoreIdsJson) {
        this.msgIgnoreIdsJson = msgIgnoreIdsJson;
    }
    public String getTopicKey() {
        return topicKey;
    }

    public void setTopicKey(String topicKey) {
        this.topicKey = topicKey;
    }
    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BotGroupTopicEntity{" +
            "id=" + id +
            ", groupId=" + groupId +
            ", senderId=" + senderId +
            ", msgStartId=" + msgStartId +
            ", msgEndId=" + msgEndId +
            ", msgIgnoreIdsJson=" + msgIgnoreIdsJson +
            ", topicKey=" + topicKey +
            ", status=" + status +
            ", type=" + type +
            ", title=" + title +
            ", createTime=" + createTime +
        "}";
    }
}
