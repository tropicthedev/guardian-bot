package com.tropicoss.guardian.services.websocket.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.services.discord.Bot;
import com.tropicoss.guardian.services.websocket.Client;
import net.minecraft.server.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

import static com.tropicoss.guardian.Mod.LOGGER;

public class MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
    private final MinecraftServer minecraftServer;
    private final Config config = Config.getInstance();
    private final Client client;
    private Bot botInstance;

    public MessageHandler(MinecraftServer minecraftServer, Client client) {
        this.minecraftServer = minecraftServer;
        this.client = client;
    }

    public MessageHandler(MinecraftServer minecraftServer) {
        this.minecraftServer = minecraftServer;
        this.client = null;
    }

    private boolean isServer() {
        return Objects.equals(config.getConfig().getGeneric().getMode(), "server");
    }

    public void handleMessage(String message) {
        try {
            Gson gson = new Gson();

            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

            if (jsonObject.has("type")) {

                String messageType = jsonObject.get("type").getAsString();

                switch (messageType) {
                    case "chat":
                        handleChatMessage(gson.fromJson(message, ChatMessage.class));
                        break;
                    case "discord":
                        handleDiscordMessage(gson.fromJson(message, DiscordMessage.class));
                        break;
                    case "starting":
                        handleStartingMessage(gson.fromJson(message, StartingMessage.class));
                        break;
                    case "started":
                        handleStartedMessage(gson.fromJson(message, StartedMessage.class));
                        break;
                    case "stopping":
                        handleStoppingMessage(gson.fromJson(message, StoppingMessage.class));
                        break;
                    case "stopped":
                        handleStoppedMessage(gson.fromJson(message, StoppedMessage.class));
                        break;
                    case "login":
                        handleLoginMessage(gson.fromJson(message, LoginMessage.class));
                        break;
                    case "logout":
                        handleLogoutMessage(gson.fromJson(message, LogoutMessage.class));
                        break;
                    case "advancement":
                        handleAdvancementMessage(gson.fromJson(message, AdvancementMessage.class));
                        break;
                    case "death":
                        handleDeathMessage(gson.fromJson(message, EntityDeathMessage.class));
                        break;
                    case "command":
                        handleCommandMessage(gson.fromJson(message, CommandMessage.class));
                        break;
                    case "completion":
                        handleCompletionMessage(gson.fromJson(message, CompletionMessage.class));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + messageType);
                }
            } else {
                LOGGER.error("No messageType in JSON object");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void handleDiscordMessage(DiscordMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));
    }

    private void handleChatMessage(ChatMessage msg) {

        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendWebhook(msg.content, msg.getProfile(), msg.origin);
        }
    }

    private void handleStartingMessage(StartingMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendServerStartingMessage(msg.origin);

        }
    }

    private void handleCompletionMessage(CompletionMessage msg) {

        if (isServer()) {
            getBot().sendCompletionMessage(msg.origin, msg.command, msg.content, msg.success);
        }
    }

    private void handleStartedMessage(StartedMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendServerStartedMessage(msg.origin, msg.uptime);
        }
    }

    private void handleStoppingMessage(StoppingMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendServerStoppingMessage(msg.server);
        }
    }

    private void handleStoppedMessage(StoppedMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendServerStoppedMessage(msg.server);
        }
    }

    void handleLoginMessage(LoginMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendJoinMessage(msg.getProfile(), msg.origin);
        }
    }

    private Bot getBot() {
        if (null == botInstance) {
            this.botInstance = Bot.getBotInstance();
        }
        return botInstance;
    }

    void setBot(Bot bot) {
        this.botInstance = bot;
    }

    private void handleLogoutMessage(LogoutMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendLeaveMessage(msg.getProfile(), msg.origin);
        }
    }

    private void handleAdvancementMessage(AdvancementMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendAchievementMessage(msg.getProfile(), msg.origin, msg.title, msg.description);
        }
    }

    private void handleDeathMessage(EntityDeathMessage msg) {
        LOGGER.info(msg.toConsoleString());

        minecraftServer
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if (isServer()) {
            getBot().sendDeathMessage(msg.origin, msg.message, msg.coordinates);
        }
    }

    private void handleCommandMessage(CommandMessage message) {
        GameProfile profile = new GameProfile(UUID.fromString(message.uuid), message.name);

        switch (message.action) {
            case "add":
                try {
                    Whitelist whitelist = minecraftServer.getPlayerManager().getWhitelist();

                    if (whitelist.isAllowed(profile)) {
                        LOGGER.error("Member {} could not be added to the whitelist", profile.getName());

                        throw new Exception("Member " + profile.getName() + " could not be added to the whitelist");
                    }

                    WhitelistEntry whitelistEntry = new WhitelistEntry(profile);

                    whitelist.add(whitelistEntry);

                    LOGGER.info("Member {} has been added to the whitelist", profile.getName());

                    sendCompletionMessage(profile.getName() + " was added to the whitelist", "add", true);

                } catch (Exception e) {

                    LOGGER.error("{} could not be added to the whitelist: {}", profile.getName(), e.getMessage());

                    sendCompletionMessage(profile.getName() + " could not be added to the whitelist", "add", false);
                }

                break;
            case "remove":
                Whitelist whitelist = minecraftServer.getPlayerManager().getWhitelist();

                try {
                    if (!whitelist.isAllowed(profile)) {
                        LOGGER.error("Member {} could not be removed from the whitelist, not allowed", profile.getName());

                        throw new Exception("Member " + profile.getName() + " could not be removed from the whitelist, not allowed");
                    }

                    WhitelistEntry whitelistEntry = new WhitelistEntry(profile);

                    whitelist.remove(whitelistEntry);

                    LOGGER.info("Member {} has been removed from the whitelist", profile.getName());

                    sendCompletionMessage(profile.getName() + " was removed from the whitelist", "remove", true);

                } catch (Exception e) {
                    LOGGER.error("An error occurred while removing {} from the whitelist: {}", profile.getName(), e.getMessage());

                    sendCompletionMessage(profile.getName() + " could not be removed from the whitelist", "remove", false);
                }

                break;
            case "ban":
                try {
                    BannedPlayerList bannedPlayerList = minecraftServer.getPlayerManager().getUserBanList();

                    if (bannedPlayerList.contains(profile)) {

                        LOGGER.warn("{} is already banned", profile.getName());

                        throw new Exception(profile.getName() + " is already banned");
                    }

                    BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(
                            profile,
                            null,
                            null,
                            null,
                            null
                    );

                    bannedPlayerList.add(bannedPlayerEntry);

                    ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(profile.getId());

                    if (serverPlayerEntity != null) {

                        serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.banned"));

                    }

                    LOGGER.info("{} has been banned ", profile.getName());

                    sendCompletionMessage(profile.getName() + " was banned from the server", "remove", true);

                } catch (Exception e) {

                    LOGGER.error("{} could Not Be Banned From The Server: {}", profile.getName(), e.getMessage());

                    sendCompletionMessage(profile.getName() + " could not be banned from the server", "remove", false);
                }

                break;
        }
    }

    private void sendCompletionMessage(String content, String command, boolean success) {
        if (client != null) {
            Gson gson = new Gson();
            CompletionMessage completionMessage = new CompletionMessage(Config.getInstance().getConfig().getGeneric().getName(), content, success, command);
            String jsonMessage = gson.toJson(completionMessage);
            client.sendMessage(jsonMessage);
        } else {
            LOGGER.debug("WebSocket client not available. Skipping completion message for command: {}", command);
        }
    }

}
