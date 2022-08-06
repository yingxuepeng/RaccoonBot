# 存完整消息体，包含url，存topic的文件
ALTER TABLE `jol`.`bot_message`
ADD COLUMN `origin_content` TEXT NULL AFTER `content`;
