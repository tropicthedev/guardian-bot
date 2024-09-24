package com.tropicoss.guardian.services.discord;

import com.google.gson.JsonObject;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.services.discord.commands.ChangeCommand;
import com.tropicoss.guardian.services.discord.commands.Onboarding;
import com.tropicoss.guardian.services.discord.commands.ResetCommand;
import com.tropicoss.guardian.services.discord.events.ChatAdapter;
import com.tropicoss.guardian.services.discord.events.UserAdapter;
import com.tropicoss.guardian.services.PlayerInfoFetcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static com.tropicoss.guardian.Mod.LOGGER;
import static com.tropicoss.guardian.Mod.MINECRAFT_SERVER;

public class Bot {

    private static Bot BOT_INSTANCE;
    private final DatabaseManager databaseManager;
    private final JDA bot;
    private final TextChannel textChannel;
    private final TextChannel consoleChannel;
    private final String iconUrl = "https://cdn2.iconfinder.com/data/icons/whcompare-isometric-web-hosting-servers/50/value-server-512.png";
    private Webhook webhook = null;

    private Bot() throws InterruptedException, SQLException {
        this.databaseManager = new DatabaseManager();
        try {
            Config config = Config.getInstance();
            bot = JDABuilder.createDefault(config.getConfig().getBot().getToken())
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(
                            new Onboarding(databaseManager),
                            new ChangeCommand(databaseManager),
                            new ResetCommand(),
                            new UserAdapter(),
                            new ChatAdapter(
                                    config.getConfig().getGeneric().getMode(),
                                    config.getConfig().getBot().getChannel(),
                                    MINECRAFT_SERVER
                            )
                    )
                    .build()
                    .awaitReady();

            textChannel = bot.getTextChannelById(config.getConfig().getBot().getChannel());
            consoleChannel = bot.getTextChannelById(config.getConfig().getBot().getConsoleChannel());

            for (Webhook webhook : textChannel.getGuild().retrieveWebhooks().complete()) {
                if ("Guardian".equals(webhook.getName())) {
                    this.webhook = webhook;
                }
            }

            if (webhook == null) {
                webhook = textChannel.createWebhook("Guardian").complete();
            }

            Guild guild = bot.getGuildById(config.getConfig().getBot().getGuild());

            if (guild != null) {
                guild.upsertCommand(
                        Commands.slash("welcome", "Creates a discord embed for new users to start onboarding")
                ).queue();

                guild.upsertCommand("reset", "Resets user application timeout allowing them to submit another application")
                        .addOption(OptionType.USER, "member", "The user that you want to reset their application timeout", true)
                        .queue();

                guild.upsertCommand("accept", "Accepts a user within an interview channel")
                        .addOption(OptionType.STRING, "ign", "The Minecraft username of the user that you want to accept", true)
                        .queue();

                guild.upsertCommand("change", "Changes the users linked minecraft account")
                        .addOption(OptionType.STRING, "ign", "The Minecraft username of the user that you want to accept", true)
                        .queue();
            }

        } catch (Exception e) {
            switch (e.getClass().getSimpleName()) {
                case "InvalidTokenException":
                    LOGGER.error("Invalid bot token. Please check your config file.");
                    break;
                case "IllegalArgumentException":
                    LOGGER.error("Invalid bot channel. Please check your config file.");
                    break;
                default:
                    LOGGER.error("Error starting bot: {}", e.getMessage());
                    break;
            }
            throw e;
        }
    }

    public static Bot getBotInstance() {
        if (null == BOT_INSTANCE) {
            try {
                BOT_INSTANCE = new Bot();

            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return BOT_INSTANCE;
    }

    public void shutdown() {
        try {
            databaseManager.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        bot.shutdown();
    }

    public void sendWebhook(String message, PlayerInfoFetcher.Profile profile, String serverName) {
        try {
            JsonObject body = new JsonObject();

            body.addProperty("username", String.format("%s - %s", profile.data.player.username, serverName));
            body.addProperty("content", message);
            body.addProperty("avatar_url", profile.data.player.avatar);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhook.getUrl()))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void sendServerStartingMessage(String serverName) {
        textChannel
                .sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(serverName, null, iconUrl)
                                .setDescription("Server is starting...")
                                .setTimestamp(Instant.now())
                                .setFooter(serverName, iconUrl)
                                .setColor(Color.ORANGE)
                                .build())
                .queue();
    }

    public void sendServerStartedMessage(String serverName, Long uptime) {
        String description = String.format("Server started in %sS 🕛", uptime / 1000);

        textChannel
                .sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(serverName, null, iconUrl)
                                .setDescription(description)
                                .setTimestamp(Instant.now())
                                .setFooter(serverName, iconUrl)
                                .setColor(Color.GREEN)
                                .build())
                .queue();
    }

    public void sendServerStoppingMessage(String serverName) {
        textChannel
                .sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(serverName, null, "https://cdn2.iconfinder.com/data/icons/whcompare-isometric-web-hosting-servers/50/value-server-512.png")
                                .setTitle("Server is stopping...")
                                .setTimestamp(Instant.now())
                                .setFooter(serverName, iconUrl)
                                .setColor(Color.ORANGE)
                                .build())
                .queue();
    }

    public void sendServerStoppedMessage(String serverName) {
        textChannel
                .sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(serverName, null, "https://cdn2.iconfinder.com/data/icons/whcompare-isometric-web-hosting-servers/50/value-server-512.png")
                                .setTitle("Server stopped!")
                                .setTimestamp(Instant.now())
                                .setFooter(serverName, iconUrl)
                                .setColor(Color.RED)
                                .build())
                .queue();
    }

    public void sendJoinMessage(PlayerInfoFetcher.Profile profile, String serverName) {

        String nameMCProfile = String.format("https://namemc.com/profile/%s", profile.data.player.username);

        textChannel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setAuthor(profile.data.player.username, nameMCProfile, profile.data.player.avatar)
                        .setTitle("Joined the server")
                        .setTimestamp(Instant.now())
                        .setFooter(serverName, iconUrl)
                        .setColor(Color.BLUE)
                        .build()
        ).queue();
    }

    public void sendLeaveMessage(PlayerInfoFetcher.Profile profile, String serverName) {
        String nameMCProfile = String.format("https://namemc.com/profile/%s", profile.data.player.username);

        textChannel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setAuthor(profile.data.player.username, nameMCProfile, profile.data.player.avatar)
                        .setTitle("Left the server")
                        .setTimestamp(Instant.now())
                        .setFooter(serverName, iconUrl)
                        .setColor(Color.orange)
                        .build()
        ).queue();
    }

    public void sendAchievementMessage(PlayerInfoFetcher.Profile profile, String serverName, String title, String description) {
        String nameMCProfile = String.format("https://namemc.com/profile/%s", profile.data.player.username);

        textChannel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setAuthor(profile.data.player.username, nameMCProfile, profile.data.player.avatar)
                        .setTitle("Got An Advancement")
                        .addField("Advancement", title, false)
                        .addField("Description", description, false)
                        .setTimestamp(Instant.now())
                        .setFooter(serverName, iconUrl)
                        .setColor(Color.BLUE)
                        .build()
        ).queue();
    }

    public void sendDeathMessage(String origin, String message, String coordinates) {

        String description = String.format("%s\n%s", message, coordinates);

        textChannel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setAuthor(origin, null, iconUrl)
                        .setDescription(description)
                        .setTimestamp(Instant.now())
                        .setFooter(origin, iconUrl)
                        .setColor(Color.BLUE)
                        .build()
        ).queue();
    }

    public void sendCompletionMessage(String serverName, String command, String message, boolean success) {
        Color color = success ? Color.green : Color.red;
        textChannel
                .sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(bot.getSelfUser().getAsTag(), null, bot.getSelfUser().getAvatarUrl())
                                .setTitle("Command: " + command)
                                .setDescription(message)
                                .setTimestamp(Instant.now())
                                .setFooter(serverName, iconUrl)
                                .setColor(color)
                                .build())
                .queue();
    }

    public void removeInactiveMember(com.tropicoss.guardian.model.Member member) {
        try {
            Guild guild = bot.getGuildById(Config.getInstance().getConfig().getBot().getGuild());

            if (guild == null) {
                LOGGER.error("Guild could not be found, check your configuration");
                return;
            }

            Member guildMember = guild.getMemberById(member.getDiscordId());

            if (guildMember == null) {
                LOGGER.error("Member could not be found, are they still apart of the server ?");
                return;
            }

            List<Role> memberRoles = guildMember.getRoles();

            Role vacationRole = guild.getRoleById(Config.getInstance().getConfig().getMember().getVacationRole());

            if (vacationRole != null && memberRoles.contains(vacationRole)) {
                LOGGER.info("Skipping member as they have the vacation role");
                return;
            }

            guild.kick(guildMember).reason("Kicked for inactivity").queue();

            MessageEmbed messageEmbed = new EmbedBuilder()
                    .setAuthor(this.bot.getSelfUser().getName(), null, iconUrl)
                    .setTitle("Member Removed")
                    .addField("Discord Name", guildMember.getUser().getAsMention(), false)
                    .addField("IGN", member.getPlayerProfile().data.player.username, false)
                    .setColor(Color.green)
                    .build();

            consoleChannel.sendMessageEmbeds(messageEmbed).queue();

        } catch (Exception e) {
            LOGGER.error("There was an error while trying to remove inactive member: {}", e.getMessage());
        }
    }
}