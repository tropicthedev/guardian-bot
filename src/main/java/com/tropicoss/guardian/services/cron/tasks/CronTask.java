package com.tropicoss.guardian.services.cron.tasks;

public interface CronTask {
    void execute();

    String getCronExpression();
}