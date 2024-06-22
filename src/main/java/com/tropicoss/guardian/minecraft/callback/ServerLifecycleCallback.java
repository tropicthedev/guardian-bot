package com.tropicoss.guardian.minecraft.callback;

import com.google.gson.Gson;

import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.networking.Server;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import  com.tropicoss.guardian.networking.messaging.StartingMessage;
import  com.tropicoss.guardian.networking.messaging.StartedMessage;
import  com.tropicoss.guardian.networking.messaging.StoppingMessage;
import  com.tropicoss.guardian.networking.messaging.StoppedMessage;
import com.tropicoss.guardian.minecraft.Commands;
import  com.tropicoss.guardian.networking.Client;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.net.URI;

import static com.tropicoss.guardian.Guardian.*;

public class ServerLifecycleCallback implements ServerLifecycleEvents.ServerStarting,
        ServerLifecycleEvents.ServerStarted,
        ServerLifecycleEvents.ServerStopping,
        ServerLifecycleEvents.ServerStopped {

    @Override
    public void onServerStarted(MinecraftServer server) {

        try {
            RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

            long uptime = rb.getUptime();

            switch (CONFIG_MANAGER.getSetting("generic", "mode")) {
                case "server", "standalone" -> Bot.getBotInstance().sendServerStartedMessage(CONFIG_MANAGER.getSetting("generic", "serverName"), uptime);

                case "client" -> {

                    StartedMessage message = new StartedMessage(CONFIG_MANAGER.getSetting("generic", "serverName"), uptime);

                    String json = new Gson().toJson(message);

                    SOCKET_CLIENT.send(json);

                    LOGGER.info("Running in Client Mode");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onServerStarting(MinecraftServer server) {
        MINECRAFT_SERVER = server;

        try {
            switch (CONFIG_MANAGER.getSetting("generic", "mode")) {
                case "server" -> {
                    Bot.getBotInstance().sendServerStartingMessage(CONFIG_MANAGER.getSetting("generic", "mode"));

                    SOCKET_SERVER = new Server(new InetSocketAddress(Integer.parseInt(CONFIG_MANAGER.getSetting("server", "port"))));

                    SOCKET_SERVER.start();

                    LOGGER.info("Running in Server Mode");
                }

                case "client" -> {
                    Commands.register();

                    String uri = String.format("ws://%s:%s", CONFIG_MANAGER.getSetting("server", "host"), CONFIG_MANAGER.getSetting("server", "port"));

                    SOCKET_CLIENT = new Client(URI.create(uri));

                    try {
                        SOCKET_CLIENT.connectBlocking();
                    } catch (InterruptedException e) {
                        LOGGER.error("There was an error connecting to Alfred Server is it running ?");
                    }

                    StartingMessage message = new StartingMessage(CONFIG_MANAGER.getSetting("generic", "serverName"));

                    String json = new Gson().toJson(message);

                    SOCKET_CLIENT.send(json);

                    LOGGER.info("Running in Client Mode");
                }

                case "standalone" -> {
                    Bot.getBotInstance().sendServerStartingMessage(CONFIG_MANAGER.getSetting("generic", "serverName"));

                    LOGGER.info("Running in Standalone Mode");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onServerStopping(MinecraftServer server) {

        try {
            StoppingMessage message = new StoppingMessage(CONFIG_MANAGER.getSetting("generic", "serverName"));

            String json = new Gson().toJson(message);

            switch (CONFIG_MANAGER.getSetting("generic", "mode")) {
                case "server" -> {
                    Bot.getBotInstance().sendServerStoppingMessage(CONFIG_MANAGER.getSetting("generic", "serverName"));

                    SOCKET_SERVER.broadcast(json);
                }

                case "client" -> SOCKET_CLIENT.send(json);

                case "standalone" -> Bot.getBotInstance().sendServerStoppingMessage(CONFIG_MANAGER.getSetting("generic", "serverName"));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onServerStopped(MinecraftServer server) {
        StoppedMessage message = new StoppedMessage(CONFIG_MANAGER.getSetting("generic", "serverName"));

        String json = new Gson().toJson(message);

        try {
            switch (CONFIG_MANAGER.getSetting("generic", "mode")) {
                case "server" -> {
                    try {
                        Bot.getBotInstance().sendServerStoppedMessage(CONFIG_MANAGER.getSetting("generic", "serverName"));

                        Bot.getBotInstance().shutdown();

                        SOCKET_SERVER.broadcast(json);

                        SOCKET_SERVER.stop(100);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error closing server: " + e.getMessage());
                    }
                }

                case "client" -> {

                    SOCKET_CLIENT.send(json);

                    SOCKET_CLIENT.closeBlocking();
                }

                case "standalone" -> {
                    Bot.getBotInstance().sendServerStoppedMessage(CONFIG_MANAGER.getSetting("generic", "serverName"));

                    Bot.getBotInstance().shutdown();
                }
            }
        }catch (InterruptedException e) {
            LOGGER.error("Error closing client: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
