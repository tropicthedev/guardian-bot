package com.tropicoss.guardian.services.discord.adapters;

import com.google.gson.Gson;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.model.ButtonId;
import com.tropicoss.guardian.model.Member;
import com.tropicoss.guardian.model.Status;
import com.tropicoss.guardian.services.Cache;
import com.tropicoss.guardian.services.PlayerInfoFetcher;
import com.tropicoss.guardian.services.chatsync.message.CommandMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.tropicoss.guardian.Mod.LOGGER;
import static com.tropicoss.guardian.Mod.socketServer;

public class SlashCommandsAdapter extends ListenerAdapter {
    private final DatabaseManager databaseManager;
    private final Config config = Config.getInstance();

    public SlashCommandsAdapter(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) return;

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient Permissions").setEphemeral(true).queue();
            return;
        }

        try {
            switch (event.getName()) {
                case "change" -> {
                    onChangeCommand(event);
                    break;
                }
                case "reset" -> {
                    onResetCommand(event);
                    break;
                }
                case "accept" -> {
                    onAcceptCommand(event);
                    break;
                }
                case "deny" -> {
                    onDenyCommand(event);
                    break;
                }
                case "welcome" -> {
                    onWelcomeCommand(event);
                    break;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("There was an error while processing slash command: {}", e.getMessage());
        }
    }

    private void onChangeCommand(SlashCommandInteractionEvent event) throws SQLException {
        OptionMapping memberOptionMapping = event.getOption("member");
        OptionMapping ignOptionMapping = event.getOption("ign");

        if (memberOptionMapping == null || ignOptionMapping == null) return;

        String userId = memberOptionMapping.getAsUser().getId();

        String ign = ignOptionMapping.getAsString();

        PlayerInfoFetcher.Profile newProfile = PlayerInfoFetcher.getProfile(ign);

        if (newProfile == null || newProfile.data == null) {
            event.reply("The provided IGN could not be found in Mojang's database, try again or check the spelling of the username")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Member member = databaseManager.getMember(userId);

        PlayerInfoFetcher.Profile oldProfile = PlayerInfoFetcher.getProfile(member.getMojangId());

        if (oldProfile == null || oldProfile.data == null) {
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
        socketServer.broadcast(removeActionString);

        CommandMessage addAction = new CommandMessage(
                newProfile.data.player.raw_id,
                newProfile.data.player.username,
                "add"
        );

        String addActionString = new Gson().toJson(addAction);
        socketServer.broadcast(addActionString);

        event.reply("Member has been updated").setEphemeral(true).queue();
    }

    private void onResetCommand(SlashCommandInteractionEvent event) throws SQLException {
        OptionMapping optionMapping = event.getOption("member");

        if (optionMapping == null) return;

        String userId = optionMapping.getAsUser().getId();

        Cache.getInstance().remove("timeout::" + userId);

        if (!Cache.getInstance().containsKey("timeout::" + userId)) {
            event.reply("User's application timeout reset was successful").queue();

            return;
        }

        event.reply("Unable to reset users application timeout reset").queue();
    }

    private void onAcceptCommand(SlashCommandInteractionEvent event) throws SQLException {

        if (event.getChannel().getType() != ChannelType.GUILD_PRIVATE_THREAD) {
            event.reply("This can only be ran in a private guild thread (Interview Thread)")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        OptionMapping optionMapping = event.getOption("ign");

        if (optionMapping == null) return;

        try {

            PlayerInfoFetcher.Profile playerProfile = PlayerInfoFetcher.getProfile(optionMapping.getAsString());

            if (playerProfile == null) {
                event.reply("Member could not be found, is the IGN correct ?").setEphemeral(true).queue();
                return;
            }

            Guild guild = event.getGuild();

            String memberId = databaseManager.getMemberFromChannelId(event.getChannelId());

            net.dv8tion.jda.api.entities.Member member = event.getGuild().getMemberById(memberId);

            if (member == null) {
                event.reply("Member could not be found, are they still in the server ?").setEphemeral(true).queue();
                return;
            }

            TextChannel channel = guild.getChannelById(TextChannel.class, config.getConfig().getMember().getChannel());

            if (channel == null) {
                event.reply("The guild channel could not be found, ensure it exists and the correct id is present in the config file")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            Role role = guild.getRoleById(config.getConfig().getMember().getRole());

            if (role == null) {
                event.reply("The member role could not be found, ensure it exists and the correct id is present in the config file")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            guild.addRoleToMember(member.getUser(), role).queue();

            channel.sendMessage(config.getConfig().getMember().getMessage().replace("{member}", member.getAsMention())).queue();

            databaseManager.addInterviewResponse(UUID.randomUUID().toString(), event.getMember().getId(), event.getChannelId(),
                    "Member Accepted", Status.ACCEPTED);

            databaseManager.addMember(memberId, playerProfile.data.player.id, false);

            CommandMessage commandMessage = new CommandMessage(playerProfile.data.player.id, playerProfile.data.player.username, "add");

            String json = new Gson().toJson(commandMessage);

            if (socketServer != null) {
                socketServer.broadcast(json);
            }

            event.reply("Member Accepted").setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error("There was an error trying to accept member: {}", e.getMessage());
            event.reply("There was an error trying to accept member please try again. If the problem persists check the console for any errors that may occur.").setEphemeral(true).queue();
        }
    }

    private void onDenyCommand(SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.GUILD_PRIVATE_THREAD) {
            event.reply("This can only be ran in a private guild thread (Interview Thread)").queue();
            return;
        }
        OptionMapping reasonMapping = event.getOption("reason");

        if (reasonMapping == null) return;

        try {

            String memberId = databaseManager.getMemberFromChannelId(event.getChannelId());

            net.dv8tion.jda.api.entities.Member member = event.getGuild().getMemberById(memberId);

            if (member == null) {
                event.reply("User could not be found, are they still in the server ?").setEphemeral(true).queue();
                return;
            }

            Objects.requireNonNull(event.getMember()).getUser().openPrivateChannel().flatMap(channel ->
                    channel.sendMessage("You have been denied for the reason: " + reasonMapping.getAsString())
            ).queue();

            databaseManager.addInterviewResponse(UUID.randomUUID().toString(),
                    event.getMember().getId(),
                    event.getChannelId(),
                    "Member Denied", Status.DENIED);

            event.reply("Member Denied").setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error("There was an error trying to deny member: {}", e.getMessage());
            event.reply("There was an error trying to deny member please try again. If the problem persists check the console for any errors that may occur.").setEphemeral(true).queue();
        }
    }


    private void onWelcomeCommand(SlashCommandInteractionEvent event)  {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder
                .setTitle("Beep Boop")
                .setColor(Color.BLUE)
                .setDescription(Config.getInstance().getConfig().getWelcome().getMessage())
                .setTimestamp(Instant.now());

        TextChannel textChannel = event.getJDA().getTextChannelById(Config.getInstance().getConfig().getWelcome().getChannel());

        if (textChannel == null) {
            event.reply("Welcome channel could not be found, ensure that the channel id is correct and that it is a TEXT CHANNEL").setEphemeral(true).queue();

            LOGGER.error("Welcome channel could not be found, ensure that the channel id is correct and that it is a TEXT CHANNEL");

            return;
        }

        textChannel.sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.primary(
                                ButtonId.APPLY,
                                "Apply"
                        ).withEmoji(Emoji.fromFormatted("\uD83D\uDCDC"))
                )
                .queue();

        event.reply("Welcome embed has been sent to welcome channel").setEphemeral(true).queue();
    }
}
