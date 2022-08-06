package com.raccoon.qqbot.data.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageSource;

@Getter
@Setter
@AllArgsConstructor
public class SourceMsgVo extends BaseMsgVo {
    private long id;

    public static SourceMsgVo Create(MessageSource source) {
        SourceMsgVo msg = new SourceMsgVo(source.getIds()[0]);
        msg.setType(MsgType.SOURCE);
        return msg;
    }
}
