package com.tropicoss.guardian.discord;

import com.google.gson.JsonObject;
import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.discord.commands.OnboardingCommand;
import com.tropicoss.guardian.discord.events.ChatAdapter;
import com.tropicoss.guardian.discord.events.UserAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
import java.time.Instant;

import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private static Bot BOT_INSTANCE;

    private final JDA bot;
    private final TextChannel textChannel;
    private final String iconUrl = "https://cdn2.iconfinder.com/data/icons/whcompare-isometric-web-hosting-servers/50/value-server-512.png";
    private final ConfigurationManager configurationManager;
    private Webhook webhook = null;

    private Bot() throws InterruptedException {
        this(null);
    }

    private Bot(ConfigurationManager configurationManager) throws InterruptedException {
        this.configurationManager = configurationManager;
        try {
            bot = JDABuilder.createDefault(getConfigManager().getSetting("bot", "token"))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new OnboardingCommand(), new UserAdapter(), new ChatAdapter(
                            this.configurationManager.getSetting("generic", "mode"),
                            this.configurationManager.getSetting("bot", "chatChannel")))
                    .build()
                    .awaitReady();

            textChannel = bot.getTextChannelById(getConfigManager().getSetting("bot", "chatChannel"));

            for (Webhook webhook : textChannel.getGuild().retrieveWebhooks().complete()) {
                if ("Alfred".equals(webhook.getName())) {
                    this.webhook = webhook;
                }
            }

            if (webhook == null) {
                webhook = textChannel.createWebhook("Alfred").complete();
            }

            Guild guild = bot.getGuildById(getConfigManager().getSetting("bot","guildId"));

            if (guild != null) {
                guild.upsertCommand(
                        Commands.slash("welcome", "Creates a discord embed for new users to start onboarding")
                ).queue();
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
                    LOGGER.error("Error starting bot: " + e.getMessage());
                    break;
            }
            throw e;
        }
    }

    private ConfigurationManager getConfigManager() {
        return configurationManager;
    }

    public static Bot getBotInstance() {
        if(null == BOT_INSTANCE) {
            try {
                // TODO: Refactor to not use Guardian Config Manager
                BOT_INSTANCE = new Bot();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return BOT_INSTANCE;
    }

    public void shutdown() throws InterruptedException {

        bot.shutdown();

        bot.awaitShutdown();
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
                        .setAuthor(origin, null ,iconUrl)
                        .setDescription(description)
                        .setTimestamp(Instant.now())
                        .setFooter(origin, iconUrl)
                        .setColor(Color.BLUE)
                        .build()
        ).queue();
    }
}