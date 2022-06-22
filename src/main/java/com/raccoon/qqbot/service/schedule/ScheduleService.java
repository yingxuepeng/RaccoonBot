package com.raccoon.qqbot.service.schedule;

import com.raccoon.qqbot.db.consts.BotAdminActionConsts;
import com.raccoon.qqbot.db.dao.BotAdminActionDao;
import com.raccoon.qqbot.db.entity.BotAdminActionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduleService {

    @Autowired
    private BotAdminActionDao botAdminActionDao;
    @Scheduled(cron = "0 */10 * * * *")
    public void expireAdminAction() {
        Timestamp now = new Timestamp(new Date(System.currentTimeMillis()).getTime());
        List<BotAdminActionEntity> actionEntities = botAdminActionDao.selectByStatus(BotAdminActionConsts.STATUS_NORMAL);
        for (BotAdminActionEntity entity : actionEntities) {
            if (entity.getExpireTime().before(now)) {
                entity.setStatus(BotAdminActionConsts.STATUS_EXPIRE);
                botAdminActionDao.updateById(entity);
            }
        }
    }

    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public Future<?> schedule(Runnable runnable, long time, TimeUnit timeUnit){
        return this.scheduledThreadPoolExecutor.schedule(runnable, time, timeUnit);
    }
}
