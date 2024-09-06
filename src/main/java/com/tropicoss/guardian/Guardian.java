package com.tropicoss.guardian;


import com.google.gson.Gson;
import com.tropicoss.guardian.api.JavalinServer;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.minecraft.Commands;
import com.tropicoss.guardian.minecraft.event.AdvancementEvent;
import com.tropicoss.guardian.minecraft.event.EntityDeathEvents;
import com.tropicoss.guardian.minecraft.event.PlayerDeathEvents;
import com.tropicoss.guardian.networking.Client;
import com.tropicoss.guardian.networking.Server;
import com.tropicoss.guardian.networking.messaging.*;
import com.tropicoss.guardian.utils.PlayerInfoFetcher;
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

public class Guardian implements DedicatedServerModInitializer {
    private static final String MOD_ID = "Guardian";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Server SOCKET_SERVER;
    public static Client SOCKET_CLIENT;
    public static MinecraftServer MINECRAFT_SERVER;
    private final Gson gson = new Gson();
    public Config config = Config.getInstance();

    @Override
    public void onInitializeServer() {
        try {

            Path guardianConfigPath = FabricLoader.getInstance().getConfigDir().resolve("guardian");

            if (!guardianConfigPath.toFile().exists()) {

                boolean isCreated = guardianConfigPath.toFile().mkdir();

                if (!isCreated) {
                    throw new Exception("Could not create Guardian config directory");
                }
            }

            if (guardianConfigPath.toFile().exists()) {
                DatabaseManager db = new DatabaseManager();
                ClassLoader classLoader = getClass().getClassLoader();

                db.runMigrations(classLoader.getResource("migrations").getPath());

                db.close();
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

            PlayerDeathEvents.EVENT.register(this::onPlayerDeath);

            EntityDeathEvents.EVENT.register(this::onEntityDeath);

            AdvancementEvent.EVENT.register(this::onGrantCriterion);

            Commands.register();

            LOGGER.info("Guardian Has Started");

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void onServerStarting(MinecraftServer server) throws SQLException {
        MINECRAFT_SERVER = server;
        JavalinServer javalinServer = new JavalinServer();

        try {
            javalinServer.startServer();
            switch (config.getConfig().getGeneric().getMode()) {
                case "server" -> {
                    Bot.getBotInstance().sendServerStartingMessage(config.getConfig().getGeneric().getMode());

                    SOCKET_SERVER = new Server(new InetSocketAddress(config.getConfig().getServer().getPort()));

                    SOCKET_SERVER.start();

                    LOGGER.info("Running in Server Mode");
                }

                case "client" -> {
                    Commands.register();

                    String uri = String.format("ws://%s:%s", config.getConfig().getServer().getHost(), config.getConfig().getServer().getPort());

                    SOCKET_CLIENT = new Client(URI.create(uri));

                    try {
                        SOCKET_CLIENT.connectBlocking();
                    } catch (InterruptedException e) {
                        LOGGER.error("There was an error connecting to Alfred Server is it running ?");
                    }

                    StartingMessage message = new StartingMessage(config.getConfig().getGeneric().getName());

                    String json = new Gson().toJson(message);

                    SOCKET_CLIENT.send(json);

                    LOGGER.info("Running in Client Mode");
                }

                case "standalone" -> {
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

                    SOCKET_CLIENT.send(json);
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

                    SOCKET_SERVER.broadcast(json);
                }

                case "client" -> SOCKET_CLIENT.send(json);

                case "standalone" ->
                        Bot.getBotInstance().sendServerStoppingMessage(config.getConfig().getGeneric().getName());
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

                        SOCKET_SERVER.broadcast(json);

                        SOCKET_SERVER.stop(100);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error closing server: {}", e.getMessage());
                    }
                }

                case "client" -> {

                    SOCKET_CLIENT.send(json);

                    SOCKET_CLIENT.closeBlocking();
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

                SOCKET_SERVER.broadcast(json);
            }

            case "client" -> SOCKET_CLIENT.send(json);

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

                SOCKET_SERVER.broadcast(json);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendLeaveMessage(profile, (config.getConfig().getGeneric().getName()));
                }
            }
        }
    }

    public void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
        try {
            ChatMessage msg = new ChatMessage((config.getConfig().getGeneric().getName()), sender.getUuid().toString(), message.getContent().getString());

            String json = new Gson().toJson(msg);

            switch (config.getConfig().getGeneric().getMode()) {
                case "server" -> {
                    Bot.getBotInstance().sendWebhook(message.getContent().getString(), msg.getProfile(), (config.getConfig().getGeneric().getName()));

                    SOCKET_SERVER.broadcast(json);
                }

                case "standalone" ->
                        Bot.getBotInstance().sendWebhook(message.getContent().getString(), msg.getProfile(), (config.getConfig().getGeneric().getName()));

                case "client" -> SOCKET_CLIENT.send(json);
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
                SOCKET_SERVER.broadcast(json);

                Bot.getBotInstance().sendDeathMessage(config.getConfig().getGeneric().getName(), message, coordinates);
            }

            case "client" -> SOCKET_CLIENT.send(json);

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
                SOCKET_SERVER.broadcast(json);

                Bot.getBotInstance().sendDeathMessage(config.getConfig().getGeneric().getName(), message, coordinates);
            }

            case "client" -> SOCKET_CLIENT.send(json);

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
                SOCKET_SERVER.broadcast(json);

                Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" ->
                    Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
        }
    }
}
