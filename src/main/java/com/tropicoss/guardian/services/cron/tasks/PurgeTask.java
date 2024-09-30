package com.tropicoss.guardian.services.cron.tasks;

import com.google.gson.Gson;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.model.Member;
import com.tropicoss.guardian.services.discord.Bot;
import com.tropicoss.guardian.services.chatsync.message.CommandMessage;
import com.tropicoss.guardian.services.PlayerInfoFetcher;

import java.util.List;

import static com.tropicoss.guardian.Mod.LOGGER;
import static com.tropicoss.guardian.Mod.socketServer;

public class PurgeTask implements CronTask {
    @Override
    public void execute() {
        LOGGER.info("Running purge cron task");

        try {
            DatabaseManager databaseManager = new DatabaseManager();

            List<Member> inactiveMembers = databaseManager.removeInactiveMembers(Config.getInstance().getConfig().getMember().getInactivityThreshold());

            databaseManager.close();

            for (Member member : inactiveMembers) {

                PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(member.getMojangId());

                if (Config.getInstance().getConfig().getGeneric().getMode() == "server") {

                    CommandMessage commandMessage = new CommandMessage(
                            member.getMojangId().replace("-", ""),
                            profile.data.player.username,
                            "remove"
                    );

                    String commandMesssageString = new Gson().toJson(commandMessage);
                    socketServer.broadcast(commandMesssageString);

                }

                Bot.getBotInstance().removeInactiveMember(member);
            }

        } catch (Exception e) {
            LOGGER.error("There was an error while running cron task to remove members: {}", e.getMessage());
        }
    }

    @Override
    public String getCronExpression() {
        return Config.getInstance().getConfig().getMember().getCron();
    }
}
