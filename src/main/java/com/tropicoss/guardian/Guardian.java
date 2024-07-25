package com.tropicoss.guardian;


import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.minecraft.callback.*;
import com.tropicoss.guardian.minecraft.event.PlayerDeathEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import com.tropicoss.guardian.minecraft.event.EntityDeathEvents;

import com.tropicoss.guardian.networking.Client;
import com.tropicoss.guardian.networking.Server;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Guardian implements DedicatedServerModInitializer {
    public static Server SOCKET_SERVER;
    public static Client SOCKET_CLIENT;
    private static final String MOD_ID = "Guardian";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeServer() {
        try {

            Path guardianConfigPath = FabricLoader.getInstance().getConfigDir().resolve("guardian");

            Config config = Config.getInstance();

            if (!guardianConfigPath.toFile().exists()) {

                boolean isCreated = guardianConfigPath.toFile().mkdir();

                if (!isCreated) {
                    throw new Exception("Could not create Guardian config directory");
                }

                DatabaseManager databaseManager = new DatabaseManager(guardianConfigPath.resolve("elder.db").toString());

                databaseManager.createDatabases();
            }

            ServerLifecycleCallback serverLifecycleCallback = new ServerLifecycleCallback(
                    config.getConfig().getServer().getHost(),
                    config.getConfig().getGeneric().getName(),
                    config.getConfig().getGeneric().getMode(),
                    config.getConfig().getServer().getPort()
            );

            ServerPlayerConnectionCallback serverPlayerConnectionCallback = new ServerPlayerConnectionCallback(
                    config.getConfig().getServer().getHost(),
                    config.getConfig().getGeneric().getName(),
                    config.getConfig().getGeneric().getMode(),
                    config.getConfig().getServer().getPort()
            );

            ServerMessageCallback serverMessageCallback = new ServerMessageCallback(
                    config.getConfig().getServer().getHost(),
                    config.getConfig().getGeneric().getName(),
                    config.getConfig().getGeneric().getMode(),
                    config.getConfig().getServer().getPort()
            );

            AdvancementCallback advancementCallback = new AdvancementCallback();

            EntityDeathCallback entityDeathCallback = new EntityDeathCallback(
                    config.getConfig().getServer().getHost(),
                    config.getConfig().getGeneric().getName(),
                    config.getConfig().getGeneric().getMode(),
                    config.getConfig().getServer().getPort(),
                    Bot.getBotInstance()
            );

            ServerLifecycleEvents.SERVER_STARTING.register(serverLifecycleCallback);

            ServerLifecycleEvents.SERVER_STARTED.register(serverLifecycleCallback);

            ServerLifecycleEvents.SERVER_STOPPING.register(serverLifecycleCallback);

            ServerLifecycleEvents.SERVER_STOPPED.register(serverLifecycleCallback);

            ServerPlayConnectionEvents.JOIN.register(serverPlayerConnectionCallback);

            ServerPlayConnectionEvents.DISCONNECT.register(serverPlayerConnectionCallback);

            ServerMessageEvents.CHAT_MESSAGE.register(serverMessageCallback);

            AdvancementCallback.EVENT.register(advancementCallback);

            PlayerDeathEvents.EVENT.register(entityDeathCallback);

            EntityDeathEvents.EVENT.register(entityDeathCallback);

            LOGGER.info("Guardian Has Started");

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
