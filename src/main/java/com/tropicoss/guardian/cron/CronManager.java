package com.tropicoss.guardian.cron;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.tropicoss.guardian.cron.tasks.CronTask;

import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class CronManager {
    private final List<CronTask> tasks = new ArrayList<>();
    private final Timer timer = new Timer(true); // Daemon timer

    public void addTask(CronTask task) {
        tasks.add(task);
        scheduleTask(task);
    }

    private void scheduleTask(CronTask task) {
        String cronExpression = task.getCronExpression();
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        CronParser parser = new CronParser(cronDefinition);
        com.cronutils.model.Cron cron = parser.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task.execute();
                rescheduleTask(executionTime);
            }
        };

        // Schedule first execution
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextExecution = executionTime.nextExecution(now).orElse(now);
        Duration delay = Duration.between(now, nextExecution);
        timer.schedule(timerTask, delay.toMillis());
    }

    private void rescheduleTask(ExecutionTime executionTime) {
        // Reschedule task
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextExecution = executionTime.nextExecution(now).orElse(now);
        Duration delay = Duration.between(now, nextExecution);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Execute task and reschedule
                tasks.forEach(CronTask::execute);
                rescheduleTask(executionTime);
            }
        }, delay.toMillis());
    }

    public void start() {
        LOGGER.info("Cron Manager has started");
    }
}
