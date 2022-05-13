package com.raccoon.qqbot.service;

//import com.raccoon.qqbot.db.dao.SolutionDao;

import com.raccoon.qqbot.db.entity.BotUsedInvcodeEntity;
import com.raccoon.qqbot.db.entity.SolutionEntity;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.springframework.stereotype.Service;

@Service
public class GroupJoinService extends BaseService {

    public void handleJoinRequest(MemberJoinRequestEvent event) {
        String[] split = event.getMessage().trim().split("答案：");
        if (split.length < 2) {
            event.reject(false, "bot邀请码没查到,#开头转人工");
            return;
        }
        String invCode = split[1];
        if (invCode.startsWith("#")) {
            return;
        }
        SolutionEntity solutionEntity = solutionDao.getByUuid(invCode);
        if (solutionEntity == null) {
            event.reject(false, "bot邀请码没查到,#开头转人工");
            return;
        }

        BotUsedInvcodeEntity botUsedInvcodeEntity = botUsedInvcodeDao.selectByInvcode(invCode);
        if (botUsedInvcodeEntity == null) {
            botUsedInvcodeEntity = new BotUsedInvcodeEntity();
            botUsedInvcodeEntity.setIsDel(false);
            botUsedInvcodeEntity.setMemberId(event.getFromId());
            botUsedInvcodeEntity.setInvcode(invCode);
            botUsedInvcodeEntity.setSolutionId(solutionEntity.getSolutionId());
            botUsedInvcodeDao.insert(botUsedInvcodeEntity);
        } else {
            // 使用的是已经验证过的邀请码，暂时不处理
        }
    }

    public void sendWelcomeMessage(MemberJoinEvent event) {
        SolutionEntity solutionEntity = getNewMemberSolutionEntity(event.getMember().getId());
        String title = getMemberTitle(0);
        if (solutionEntity != null) {
            title = getMemberTitle(solutionEntity.getProblemId());
        }
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new PlainText("欢迎新" + title + "! "));
        builder.append(new At(event.getMember().getId()));
        builder.append(new PlainText("\n请熟读群公告规定，并修改群名片为：'{昵称}_{最好的语言}'!"));
        miraiBot.getGroup(miraiInfo.getGroupId()).sendMessage(builder.build());


        if (solutionEntity == null) {
            return;
        }
        // url
        String url = "新群友代码地址：\n" + "http://www.primeoj.com/uuid.php?uuid=" + solutionEntity.getSolutionUuid();
        miraiBot.getGroup(miraiInfo.getGroupId()).sendMessage(new PlainText(url));
    }

    private SolutionEntity getNewMemberSolutionEntity(long qid) {
        BotUsedInvcodeEntity botUsedInvcodeEntity = botUsedInvcodeDao.selectByMemberId(qid);
        if (botUsedInvcodeEntity == null) {
            return null;
        }

        return solutionDao.selectById(botUsedInvcodeEntity.getSolutionId());
    }

    private String getMemberTitle(int problemId) {
        if (problemId == 1000) {
            return "码皇";
        } else if (problemId == 1001) {
            return "码猴";
        } else if (problemId == 1002) {
            return "码农";
        } else if (problemId == 1003) {
            return "码虎";
        }
        return "关系户";
    }
}
