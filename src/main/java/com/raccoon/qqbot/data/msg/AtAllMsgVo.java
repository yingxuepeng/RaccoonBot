package com.raccoon.qqbot.data.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.AtAll;

@Getter
@Setter
@AllArgsConstructor
public class AtAllMsgVo extends BaseMsgVo {

    public static AtAllMsgVo Create(AtAll atAll) {
        AtAllMsgVo msg = new AtAllMsgVo();
        msg.setType(MsgType.AT_ALL);
        return msg;
    }
}
