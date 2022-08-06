package com.raccoon.qqbot.data.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;

@Getter
@Setter
@AllArgsConstructor
public class AtMsgVo extends BaseMsgVo {
    private long target;
    private String display;

    public static AtMsgVo Create(At at, Group group) {
        AtMsgVo msg = new AtMsgVo(at.getTarget(), at.getDisplay(group));
        msg.setType(MsgType.AT);
        return msg;
    }
}
