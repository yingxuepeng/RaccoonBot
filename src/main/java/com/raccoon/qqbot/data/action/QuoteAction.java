package com.raccoon.qqbot.data.action;

import net.mamoe.mirai.message.data.QuoteReply;

public class QuoteAction extends UserAction {
    private QuoteReply quoteReply;

    public QuoteReply getQuoteReply() {
        return quoteReply;
    }

    public void setQuoteReply(QuoteReply quoteReply) {
        this.quoteReply = quoteReply;
    }
}
