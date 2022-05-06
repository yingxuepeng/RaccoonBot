package com.raccoon.qqbot.kotlin

import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory

import net.mamoe.mirai.utils.BotConfiguration

public class BotUtils {
    object Instance {
        fun String.decodeHex(): ByteArray {
            check(length % 2 == 0) { "Must have an even length" }

            return chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        }

        final fun getMiraiBot(loginQid: String, loginPwdMd5: String): Bot {
            var pwdMd5: ByteArray = loginPwdMd5.decodeHex();
            return BotFactory.newBot(loginQid.toLong(), pwdMd5, object : BotConfiguration() {
                init {
                    // 使用平板协议登录
                    protocol = MiraiProtocol.ANDROID_PAD
                    // 指定设备信息文件路径，文件不存在将自动生成一个默认的，存在就读取
                    fileBasedDeviceInfo("device.json")
                    // 更多操作自己看代码补全吧
                }
            })
        }
    }


}