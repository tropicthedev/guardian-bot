package com.tropicoss.guardian;


import com.google.gson.Gson;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.minecraft.event.AdvancementCallback;
import com.tropicoss.guardian.minecraft.event.EntityDeathCallback;
import com.tropicoss.guardian.minecraft.event.PlayerDeathCallback;
import com.tropicoss.guardian.services.api.JavalinServer;
import com.tropicoss.guardian.services.chatsync.Client;
import com.tropicoss.guardian.services.cron.CronManager;
import com.tropicoss.guardian.services.cron.tasks.PurgeTask;
import com.tropicoss.guardian.services.discord.Bot;
import com.tropicoss.guardian.services.chatsync.Server;
import com.tropicoss.guardian.services.chatsync.message.*;
import com.tropicoss.guardian.services.PlayerInfoFetcher;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Mod implements DedicatedServerModInitializer {
    private static final String MOD_ID = "Guardian";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Server socketServer;
    public static Client socketClient;
    public static MinecraftServer MINECRAFT_SERVER;
    private final Gson gson = new Gson();
    private final Path guardianConfigPath = FabricLoader.getInstance().getConfigDir().resolve("guardian");
    public Config config = Config.getInstance();
    private JavalinServer javalinServer;

    @Override
    public void onInitializeServer() {

        try {
            if (!guardianConfigPath.toFile().exists()) {

                boolean isCreated = guardianConfigPath.toFile().mkdir();

                if (!isCreated) {
                    throw new Exception("Could not create Guardian config directory");
                }
            }

            ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                try {
                    onServerStarting(server);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

            ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

            ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);

            ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);

            ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerLeave);

            ServerMessageEvents.CHAT_MESSAGE.register(this::onChatMessage);

            PlayerDeathCallback.EVENT.register(this::onPlayerDeath);

            EntityDeathCallback.EVENT.register(this::onEntityDeath);

            AdvancementCallback.EVENT.register(this::onGrantCriterion);

            LOGGER.info("Guardian Has Started");

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void onServerStarting(MinecraftServer server) throws SQLException {
        MINECRAFT_SERVER = server;

        try {
            switch (config.getConfig().getGeneric().getMode()) {
                case "server" -> {

                    if (guardianConfigPath.toFile().exists()) {
                        DatabaseManager db = new DatabaseManager();

                        db.runMigrations("migrations");

                        db.close();
                    }

                    CronManager cronManager = new CronManager();

                    cronManager.addTask(new PurgeTask());

                    cronManager.start();

                    javalinServer = new JavalinServer();

                    Bot.getBotInstance().sendServerStartingMessage(config.getConfig().getGeneric().getName());

                    socketServer = new Server(new InetSocketAddress(config.getConfig().getServer().getWebsocketPort()));

                    socketServer.start();

                    LOGGER.info("Running in Server Mode");

                    javalinServer.startServer();
                }

                case "client" -> {
                    String uri = String.format("ws://%s:%s", config.getConfig().getServer().getHost(), config.getConfig().getServer().getWebsocketPort());

                    socketClient = new Client(URI.create(uri));

                    try {
                        socketClient.connectBlocking(10, TimeUnit.SECONDS);

                        StartingMessage message = new StartingMessage(config.getConfig().getGeneric().getName());

                        String json = new Gson().toJson(message);

                        if (socketClient.isOpen()) {
                            socketClient.send(json);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.error("There was an error connecting to Alfred Server is it running ?");
                    }

                    LOGGER.info("Running in SyncClient Mode");
                }

                case "standalone" -> {
                    if (guardianConfigPath.toFile().exists()) {
                        DatabaseManager db = new DatabaseManager();

                        db.runMigrations("migrations");

                        db.close();
                    }

                    CronManager cronManager = new CronManager();

                    cronManager.addTask(new PurgeTask());

                    cronManager.start();

                    javalinServer = new JavalinServer();

                    javalinServer.startServer();

                    Bot.getBotInstance().sendServerStartingMessage(config.getConfig().getGeneric().getName());

                    LOGGER.info("Running in Standalone Mode");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void onServerStarted(MinecraftServer server) {
        try {
            RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

            long uptime = rb.getUptime();

            switch (config.getConfig().getGeneric().getMode()) {
                case "server", "standalone" ->
                        Bot.getBotInstance().sendServerStartedMessage(config.getConfig().getGeneric().getName(), uptime);

                case "client" -> {

                    StartedMessage message = new StartedMessage(config.getConfig().getGeneric().getName(), uptime);

                    String json = new Gson().toJson(message);

                    if (socketClient.isOpen()) {
                        socketClient.send(json);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void onServerStopping(MinecraftServer server) {
        try {
            StoppingMessage message = new StoppingMessage(config.getConfig().getGeneric().getName());

            String json = new Gson().toJson(message);

            switch (config.getConfig().getGeneric().getMode()) {
                case "server" -> {
                    Bot.getBotInstance().sendServerStoppingMessage(config.getConfig().getGeneric().getName());

                    socketServer.broadcast(json);
                    javalinServer.stopServer();
                }

                case "client" -> {
                    if (socketClient.isOpen()) {
                        socketClient.send(json);
                    }
                }

                case "standalone" -> {
                    Bot.getBotInstance().sendServerStoppingMessage(config.getConfig().getGeneric().getName());
                    javalinServer.stopServer();
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void onServerStopped(MinecraftServer server) {
        StoppedMessage message = new StoppedMessage(config.getConfig().getGeneric().getName());

        String json = new Gson().toJson(message);

        try {
            switch (config.getConfig().getGeneric().getMode()) {
                case "server" -> {
                    try {
                        Bot.getBotInstance().sendServerStoppedMessage(config.getConfig().getGeneric().getName());

                        Bot.getBotInstance().shutdown();

                        socketServer.broadcast(json);

                        socketServer.stop(100);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error closing server: {}", e.getMessage());
                    }
                }

                case "client" -> {

                    if (socketClient.isOpen()) {
                        socketClient.send(json);
                    }

                    socketClient.closeBlocking();
                }

                case "standalone" -> {
                    Bot.getBotInstance().sendServerStoppedMessage(config.getConfig().getGeneric().getName());

                    Bot.getBotInstance().shutdown();
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error closing client: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        LoginMessage loginMessage = new LoginMessage((config.getConfig().getGeneric().getName()), handler.player.getUuidAsString());

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(handler.player.getUuidAsString());

        String json = gson.toJson(loginMessage);

        switch (config.getConfig().getGeneric().getMode()) {
            case "server" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendJoinMessage(profile, (config.getConfig().getGeneric().getName()));
                }

                socketServer.broadcast(json);
            }

            case "client" -> {
                if (socketClient.isOpen()) {
                    socketClient.send(json);
                }
            }

            case "standalone" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendJoinMessage(profile, (config.getConfig().getGeneric().getName()));
                }
            }
        }
    }

    public void onPlayerLeave(ServerPlayNetworkHandler handler, MinecraftServer server) {
        LogoutMessage logoutMessage = new LogoutMessage((config.getConfig().getGeneric().getName()), handler.player.getUuidAsString());

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(handler.player.getUuidAsString());

        String json = gson.toJson(logoutMessage);

        switch (config.getConfig().getGeneric().getMode()) {
            case "server" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendLeaveMessage(profile, (config.getConfig().getGeneric().getName()));
                }

                socketServer.broadcast(json);
            }

            case "client" -> {
                if (socketClient.isOpen()) {
                    socketClient.send(json);
                }
            }

            case "standalone" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendLeaveMessage(profile, (config.getConfig().getGeneric().getName()));
                }
            }
        }
    }

    public void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
        try {

            String originalMessage = message.getContent().getString();

            String formattedMessage = formatMessage(originalMessage);

            message = SignedMessage.ofUnsigned(formattedMessage);

            MINECRAFT_SERVER.getPlayerManager().broadcast(message, sender, params);

            ChatMessage msg = new ChatMessage((config.getConfig().getGeneric().getName()), sender.getUuid().toString(), originalMessage);

            String json = new Gson().toJson(msg);

            switch (config.getConfig().getGeneric().getMode()) {
                case "server" -> {
                    Bot.getBotInstance().sendWebhook(originalMessage, msg.getProfile(), (config.getConfig().getGeneric().getName()));

                    socketServer.broadcast(json);
                }

                case "standalone" ->
                        Bot.getBotInstance().sendWebhook(originalMessage, msg.getProfile(), config.getConfig().getGeneric().getName());

                case "client" -> {
                    if (socketClient.isOpen()) {
                        socketClient.send(json);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void onCommandMessage(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {
        final String typeKey = params.type().value().chat().translationKey();

        switch (typeKey) {
            case "chat.type.emote" -> handleMeCommand(message, source, params);

            case "chat.type.announcement" -> handleSayCommand(message, source, params);
        }
    }

    private void handleSayCommand(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {

    }

    private void handleMeCommand(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {

    }

    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        String message = source.getDeathMessage(player).getString();

        RegistryKey<World> registry = player.getWorld().getRegistryKey();

        String dimension = registry.getValue().toString();

        // Type Casting to Int to remove decimal points
        String coordinates = String.format("*%s at %s, %s, %s*", dimension.replaceAll(".*:", ""), (int) player.getX(), (int) player.getY(), (int) player.getZ());

        EntityDeathMessage entityDeathMessage = new EntityDeathMessage(message, coordinates, config.getConfig().getGeneric().getName());

        String json = new Gson().toJson(entityDeathMessage);

        switch (config.getConfig().getGeneric().getMode()) {
            case "server" -> {
                socketServer.broadcast(json);

                Bot.getBotInstance().sendDeathMessage(config.getConfig().getGeneric().getName(), message, coordinates);
            }

            case "client" -> {
                if (socketClient.isOpen()) {
                    socketClient.send(json);
                }
            }

            case "standalone" ->
                    Bot.getBotInstance().sendDeathMessage(config.getConfig().getGeneric().getName(), message, coordinates);
        }
    }

    public void onEntityDeath(LivingEntity entity, DamageSource source) {
        if (!entity.hasCustomName()) return;

        String message = source.getDeathMessage(entity).getString();

        RegistryKey<World> registry = entity.getWorld().getRegistryKey();

        String dimension = registry.getValue().toString();

        String coordinates = String.format("*%s at %s, %s, %s*", dimension.replaceAll(".*:", ""), (int) entity.getX(), (int) entity.getY(), (int) entity.getZ());

        EntityDeathMessage entityDeathMessage = new EntityDeathMessage(message, coordinates, (config.getConfig().getGeneric().getName()));

        String json = new Gson().toJson(entityDeathMessage);

        switch (config.getConfig().getGeneric().getMode()) {
            case "server" -> {
                socketServer.broadcast(json);

                Bot.getBotInstance().sendDeathMessage(config.getConfig().getGeneric().getName(), message, coordinates);
            }

            case "client" -> {
                if (socketClient.isOpen()) {
                    socketClient.send(json);
                }
            }

            case "standalone" ->
                    Bot.getBotInstance().sendDeathMessage(config.getConfig().getGeneric().getName(), message, coordinates);
        }
    }

    public void onGrantCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) {
        AdvancementDisplay advancementDisplay = advancement.value().display().get();

        if (!advancementDisplay.shouldAnnounceToChat()) return;

        AdvancementMessage advancementMessage = new AdvancementMessage(advancementDisplay.getTitle().getString(),
                advancementDisplay.getDescription().getString(), player.getUuidAsString(),
                this.config.getConfig().getGeneric().getName());

        String json = new Gson().toJson(advancementMessage);

        switch (config.getConfig().getGeneric().getName()) {
            case "server" -> {
                socketServer.broadcast(json);

                Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
            }

            case "client" -> {
                if (socketClient.isOpen()) {
                    socketClient.send(json);
                }
            }

            case "standalone" ->
                    Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
        }
    }

    private static String formatMessage(String message) {
        return message
                .replace("**", "§l")  // Bold
                .replace("__", "§n")   // Underline
                .replace("*", "§o")    // Italics
                .replace("~~", "§m")   // Strikethrough
                .replace("`", "§k");   // Obfuscated (similar to code block)
    }
}
