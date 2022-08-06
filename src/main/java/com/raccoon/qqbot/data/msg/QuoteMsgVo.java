package com.raccoon.qqbot.data.msg;

import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.QuoteReply;

@Getter
@Setter
public class QuoteMsgVo extends BaseMsgVo {
    private MsgChainVo originMsgChain;

    public static QuoteMsgVo Create(QuoteReply quoteReply, Group group) {
        QuoteMsgVo msg = new QuoteMsgVo();
        msg.setType(MsgType.QUOTE);
        msg.originMsgChain = MsgChainVo.Create(quoteReply.getSource().getOriginalMessage(), group);
        return msg;
    }
}
