package com.raccoon.qqbot.data.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.PlainText;

@Getter
@Setter
@AllArgsConstructor
public class PlainMsgVo extends BaseMsgVo {
    private String text;

    public static PlainMsgVo Create(PlainText plainText) {
        PlainMsgVo msg = new PlainMsgVo(plainText.getContent());
        msg.setType(MsgType.PLAIN);
        return msg;
    }
}
