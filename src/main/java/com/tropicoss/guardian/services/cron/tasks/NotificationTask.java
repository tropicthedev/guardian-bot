package com.tropicoss.guardian.services.cron.tasks;

import com.tropicoss.guardian.config.Config;

import static com.tropicoss.guardian.Mod.LOGGER;

public class NotificationTask implements CronTask {
    @Override
    public void execute() {
        LOGGER.info("Running notification cron task");
    }

    @Override
    public String getCronExpression() {
        return Config.getInstance().getConfig().getMember().getCron();
    }
}
