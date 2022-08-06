package com.raccoon.qqbot.data.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;

@Getter
@Setter
@AllArgsConstructor
public class ImgMsgVo extends BaseMsgVo {
    private String imageId;
    private int width;
    private int height;
    private String url;

    public static ImgMsgVo Create(Image image) {
        ImgMsgVo msg = new ImgMsgVo(image.getImageId(), image.getWidth(), image.getHeight(), Image.queryUrl(image));
        msg.setType(MsgType.IMAGE);
        return msg;
    }
}
