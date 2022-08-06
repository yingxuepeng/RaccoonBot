package com.raccoon.qqbot.data.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MsgChainVo {

    private List<BaseMsgVo> msgItemList;

    public static MsgChainVo Create(MessageChain mc, Group group) {
        MsgChainVo msgChain = new MsgChainVo(new ArrayList<>());
        for (int i = 0; i < mc.size(); i++) {
            SingleMessage sm = mc.get(i);
            if (sm instanceof MessageSource) {
                MessageSource msg = (MessageSource) sm;
                msgChain.msgItemList.add(SourceMsgVo.Create(msg));
            } else if (sm instanceof QuoteReply) {
                QuoteReply msg = (QuoteReply) sm;
                msgChain.msgItemList.add(QuoteMsgVo.Create(msg, group));
            } else if (sm instanceof At) {
                At msg = (At) sm;
                msgChain.msgItemList.add(AtMsgVo.Create(msg, group));
            } else if (sm instanceof AtAll) {
                AtAll msg = (AtAll) sm;
                msgChain.msgItemList.add(AtAllMsgVo.Create(msg));
            } else if (sm instanceof Face) {
                Face msg = (Face) sm;
                // todo
            } else if (sm instanceof PlainText) {
                PlainText msg = (PlainText) sm;
                msgChain.msgItemList.add(PlainMsgVo.Create(msg));
            } else if (sm instanceof Image) {
                Image msg = (Image) sm;
                msgChain.msgItemList.add(ImgMsgVo.Create(msg));
            } else {
                // ignore other msg
            }
        }
        return msgChain;
    }
}
