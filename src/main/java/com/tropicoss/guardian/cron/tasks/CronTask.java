package com.tropicoss.guardian.cron.tasks;

public interface CronTask {
    void execute();

    String getCronExpression();
}