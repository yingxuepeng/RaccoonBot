package com.raccoon.qqbot.data.action;

import com.raccoon.qqbot.db.consts.BotAdminActionConsts;
import com.raccoon.qqbot.db.entity.BotAdminActionEntity;

import java.util.List;

public class QuotaShowAction extends UserAction {
    public Stat getStat(List<BotAdminActionEntity> entityList) {
        Stat stat = new Stat();
        for (BotAdminActionEntity entity : entityList) {
            if (entity.getType() == BotAdminActionConsts.TYPE_QUOTA) {
                if (entity.getQuotaCnt() > 0) {
                    stat.heartCnt += entity.getQuotaCnt();
                } else if (entity.getQuotaCnt() < 0) {
                    stat.bombCnt += -entity.getQuotaCnt();
                }
            } else if (entity.getType() == BotAdminActionConsts.TYPE_QUOTA_EXTRA) {
                stat.lifeCnt += entity.getQuotaCnt();
            }
        }
        return stat;
    }

    public static final class Stat {
        private int heartCnt;
        private int bombCnt;

        private int lifeCnt;

        public int getHeartCnt() {
            return heartCnt;
        }

        public void setHeartCnt(int heartCnt) {
            this.heartCnt = heartCnt;
        }

        public int getBombCnt() {
            return bombCnt;
        }

        public void setBombCnt(int bombCnt) {
            this.bombCnt = bombCnt;
        }

        public int getLifeCnt() {
            return lifeCnt;
        }

        public void setLifeCnt(int lifeCnt) {
            this.lifeCnt = lifeCnt;
        }
    }
}
