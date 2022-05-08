package com.raccoon.qqbot.data;

public class ScriptResultVo {
    private Boolean shouldMute;
    private Long muteMillis;

    private int msgCnt;
    private int msgQuota;

    public Boolean getShouldMute() {
        return shouldMute;
    }

    public void setShouldMute(Boolean shouldMute) {
        this.shouldMute = shouldMute;
    }

    public Long getMuteMillis() {
        return muteMillis;
    }

    public void setMuteMillis(Long muteMillis) {
        this.muteMillis = muteMillis;
    }

    public int getMsgCnt() {
        return msgCnt;
    }

    public void setMsgCnt(int msgCnt) {
        this.msgCnt = msgCnt;
    }

    public int getMsgQuota() {
        return msgQuota;
    }

    public void setMsgQuota(int msgQuota) {
        this.msgQuota = msgQuota;
    }
}
