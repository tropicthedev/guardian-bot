package com.tropicoss.guardian.services.cron.tasks;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.model.Member;
import com.tropicoss.guardian.services.discord.Bot;
import com.tropicoss.guardian.services.chatsync.message.CommandMessage;
import com.tropicoss.guardian.services.PlayerInfoFetcher;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.tropicoss.guardian.Mod.*;

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

                if (Objects.equals(Config.getInstance().getConfig().getGeneric().getMode(), "server")) {

                    if(profile == null) {
                        LOGGER.warn("Could not get player information");
                        return;
                    }

                    Whitelist whitelist = MINECRAFT_SERVER.getPlayerManager().getWhitelist();

                    GameProfile gameProfile = new GameProfile(UUID.fromString(member.getMojangId().replace("-", "")), profile.data.player.username);

                    try {
                        if (!whitelist.isAllowed(gameProfile)) {
                            LOGGER.error("Member {} could not be removed from the whitelist, not allowed", gameProfile.getName());

                            throw new Exception("Member " + gameProfile.getName() + " could not be removed from the whitelist, not allowed");
                        }

                        WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);

                        whitelist.remove(whitelistEntry);

                        LOGGER.info("Member {} has been removed from the whitelist", gameProfile.getName());


                    } catch (Exception e) {
                        LOGGER.error("An error occurred while removing {} from the whitelist: {}", gameProfile.getName(), e.getMessage());

                    }

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
