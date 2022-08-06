package com.raccoon.qqbot.data.msg;

public enum MsgType {
    SOURCE(1),
    QUOTE(2),
    AT(3),
    AT_ALL(4),
    FACE(5),
    PLAIN(6),
    IMAGE(7);

    private int type;

    MsgType(int type) {
        this.type = type;
    }
}
