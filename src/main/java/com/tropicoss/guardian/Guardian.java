package com.tropicoss.guardian;


import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.database.DatabaseManager;
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
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.SQLException;

public class Guardian implements DedicatedServerModInitializer {
    public static Server SOCKET_SERVER;
    public static Client SOCKET_CLIENT;
    public static MinecraftServer MINECRAFT_SERVER;
    public static final Logger LOGGER = LoggerFactory.getLogger(Guardian.class);
    private static final Path CONFIG_DIRECTORY = FabricLoader.getInstance().getConfigDir().resolve("guardian");
    public static ConfigurationManager CONFIG_MANAGER;

    static {
        try {
            CONFIG_MANAGER = new ConfigurationManager(CONFIG_DIRECTORY.resolve("config.json").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInitializeServer() {
        try {

            if (!FabricLoader.getInstance().getConfigDir().resolve("guardian").toFile().exists()) {
                boolean isCreated = FabricLoader.getInstance().getConfigDir().resolve("guardian").toFile().mkdir();

                if (!isCreated) {
                    throw new Exception("Could not create Guardian config directory");
                }

                DatabaseManager databaseManager = new DatabaseManager(CONFIG_DIRECTORY.resolve("elder.db").toString());

                databaseManager.createDatabases();
            }

            ServerLifecycleCallback serverLifecycleCallback = new ServerLifecycleCallback();

            ServerPlayerConnectionCallback serverPlayerConnectionCallback = new ServerPlayerConnectionCallback();

            ServerMessageCallback serverMessageCallback = new ServerMessageCallback();

            AdvancementCallback advancementCallback = new AdvancementCallback();

            EntityDeathCallback entityDeathCallback = new EntityDeathCallback();

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
