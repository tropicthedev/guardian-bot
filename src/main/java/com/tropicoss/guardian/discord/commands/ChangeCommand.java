package com.tropicoss.guardian.discord.commands;

import com.google.gson.Gson;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.model.Member;
import com.tropicoss.guardian.utils.Cache;
import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import com.tropicoss.guardian.websocket.message.CommandMessage;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.sql.SQLException;
import java.util.Objects;

import static com.tropicoss.guardian.Guardian.LOGGER;
import static com.tropicoss.guardian.Guardian.SOCKET_SERVER;

public class ChangeCommand extends ListenerAdapter {
    private final DatabaseManager databaseManager;

    public ChangeCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {
            if (Objects.requireNonNull(event.getUser()).isBot()) return;

            if (event.getName().equals("change")) {
                OptionMapping optionMapping = event.getOption("member");

                if (optionMapping == null) return;

                String userId = optionMapping.getAsUser().getId();

                String ign = event.getOption("ign").getAsString();

                PlayerInfoFetcher.Profile newProfile = PlayerInfoFetcher.getProfile(ign);

                if(newProfile == null || newProfile.data == null) {
                    event.reply("Provided IGN could not be found in Mojang's Database, try again or check the spelling of the name")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                Member member = databaseManager.getMember(userId);

                PlayerInfoFetcher.Profile oldProfile = PlayerInfoFetcher.getProfile(member.getMojangId());

                if(oldProfile == null || oldProfile.data == null) {
                    event.reply("Provided member could not be found in Mojang's Database, try again or check the ID in Guardian's database")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                CommandMessage removeAction = new CommandMessage(
                        member.getMojangId().replace("-", ""),
                        oldProfile.data.player.username,
                        "remove"
                        );

                String removeActionString = new Gson().toJson(removeAction);
                SOCKET_SERVER.broadcast(removeActionString);

                CommandMessage addAction = new CommandMessage(
                        newProfile.data.player.raw_id,
                        newProfile.data.player.username,
                        "add"
                );

                String addActionString = new Gson().toJson(addAction);
                SOCKET_SERVER.broadcast(addActionString);

                event.reply("Member has been updated").setEphemeral(true).queue();
            }

        } catch (RuntimeException e) {
            LOGGER.error("An error occurred while trying to reset users profile {}", e.getMessage());
        } catch (SQLException e) {
            LOGGER.error("An error occurred while trying to get member from data base {}", e.getMessage());
        }
    }
}
