package com.tropicoss.guardian.networking.messaging;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tropicoss.guardian.discord.Bot;

import java.util.Objects;

import static com.tropicoss.guardian.Guardian.*;

public class MessageHandler {
    private final boolean SERVER = Objects.equals(CONFIG_MANAGER.getSetting("generic", "mode"), "server");
    public void handleMessage(String message) {
        try { Gson gson =  new Gson();

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
                    default:
                        throw new IllegalStateException("Unexpected value: " + messageType);
                }
            } else {
                System.out.println("No messageType in JSON object");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void handleDiscordMessage(DiscordMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));
    }

    private void handleChatMessage(ChatMessage msg) {

        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(SERVER) {
            Bot.getInstance().sendWebhook(msg.content, msg.getProfile(), msg.origin);
        }
    }

    private void handleStartingMessage(StartingMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(SERVER) {
            Bot.getInstance().sendServerStartingMessage(msg.origin);

        }
    }

    private void handleStartedMessage(StartedMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(SERVER) {
            Bot.getInstance().sendServerStartedMessage(msg.origin, msg.uptime);
        }
    }

    private void handleStoppingMessage(StoppingMessage msg) {

        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(SERVER) {
            Bot.getInstance().sendServerStoppingMessage(msg.server);
        }
    }

    private void handleStoppedMessage(StoppedMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(SERVER) {
            Bot.getInstance().sendServerStoppedMessage(msg.server);
        }
    }

    private void handleLoginMessage(LoginMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(SERVER) {
            Bot.getInstance().sendJoinMessage(msg.getProfile(), msg.origin);
        }
    }

    private void handleLogoutMessage(LogoutMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(SERVER) {
            Bot.getInstance().sendLeaveMessage(msg.getProfile(), msg.origin);
        }
    }

    private void handleAdvancementMessage(AdvancementMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(!SERVER) {
            Bot.getInstance().sendAchievementMessage(msg.getProfile(), msg.origin, msg.title, msg.description);
        }
    }

    private void handleDeathMessage(EntityDeathMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        if(SERVER) {
            Bot.getInstance().sendDeathMessage(msg.origin, msg.message, msg.coordinates);
        }
    }
}
