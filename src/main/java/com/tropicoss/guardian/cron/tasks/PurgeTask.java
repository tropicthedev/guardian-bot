package com.tropicoss.guardian.cron.tasks;

import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.model.Member;

import java.util.List;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class PurgeTask implements CronTask {
    @Override
    public void execute() {
        LOGGER.info("Running purge cron task");

        try {
            DatabaseManager databaseManager = new DatabaseManager();

            List<Member> inactiveMembers = databaseManager.removeInactiveMembers(Config.getInstance().getConfig().getMember().getInactivityThreshold());

            databaseManager.close();

        } catch (Exception e) {
            LOGGER.error("There was an error while running cron task to remove members: {}", e.getMessage());
        }
    }

    @Override
    public String getCronExpression() {
        return Config.getInstance().getConfig().getMember().getCron();
    }
}
