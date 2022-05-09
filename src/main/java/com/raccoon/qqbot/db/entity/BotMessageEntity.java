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
@TableName("bot_message")
public class BotMessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long senderId;

    private String content;

    private Integer labelType;

    private String labelFirst;

    private String labelSecond;

    private LocalDateTime createTime;

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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
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
            ", content=" + content +
            ", labelType=" + labelType +
            ", labelFirst=" + labelFirst +
            ", labelSecond=" + labelSecond +
            ", createTime=" + createTime +
            ", isDel=" + isDel +
            ", isTrainable=" + isTrainable +
        "}";
    }
}
