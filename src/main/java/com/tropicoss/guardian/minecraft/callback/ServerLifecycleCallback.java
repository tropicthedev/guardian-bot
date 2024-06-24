package com.tropicoss.guardian.minecraft.callback;

import com.google.gson.Gson;

import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.networking.Server;
import com.tropicoss.guardian.services.MinecraftServerService;
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

public class ServerLifecycleCallback extends ServerEventCallback implements ServerLifecycleEvents.ServerStarting,
        ServerLifecycleEvents.ServerStarted,
        ServerLifecycleEvents.ServerStopping,
        ServerLifecycleEvents.ServerStopped {

    public ServerLifecycleCallback(String host, String serverName, String mode, String port) {
        super(host, serverName, mode, port);
    }

    @Override
    public void onServerStarted(MinecraftServer server) {

        try {
            RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

            long uptime = rb.getUptime();

            switch (getMode()) {
                case "server", "standalone" -> Bot.getBotInstance().sendServerStartedMessage(getServerName(), uptime);

                case "client" -> {

                    StartedMessage message = new StartedMessage(getServerName(), uptime);

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
        MinecraftServerService.setServerInstance(server);

        try {
            switch (getMode()) {
                case "server" -> {
                    Bot.getBotInstance().sendServerStartingMessage(getMode());

                    SOCKET_SERVER = new Server(new InetSocketAddress(Integer.parseInt(getPort())));

                    SOCKET_SERVER.start();

                    LOGGER.info("Running in Server Mode");
                }

                case "client" -> {
                    Commands.register();

                    String uri = String.format("ws://%s:%s", getHost(), getPort());

                    SOCKET_CLIENT = new Client(URI.create(uri));

                    try {
                        SOCKET_CLIENT.connectBlocking();
                    } catch (InterruptedException e) {
                        LOGGER.error("There was an error connecting to Alfred Server is it running ?");
                    }

                    StartingMessage message = new StartingMessage(getServerName());

                    String json = new Gson().toJson(message);

                    SOCKET_CLIENT.send(json);

                    LOGGER.info("Running in Client Mode");
                }

                case "standalone" -> {
                    Bot.getBotInstance().sendServerStartingMessage(getServerName());

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
            StoppingMessage message = new StoppingMessage(getServerName());

            String json = new Gson().toJson(message);

            switch (getMode()) {
                case "server" -> {
                    Bot.getBotInstance().sendServerStoppingMessage(getServerName());

                    SOCKET_SERVER.broadcast(json);
                }

                case "client" -> SOCKET_CLIENT.send(json);

                case "standalone" -> Bot.getBotInstance().sendServerStoppingMessage(getServerName());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onServerStopped(MinecraftServer server) {
        StoppedMessage message = new StoppedMessage(getServerName());

        String json = new Gson().toJson(message);

        try {
            switch (getMode()) {
                case "server" -> {
                    try {
                        Bot.getBotInstance().sendServerStoppedMessage(getServerName());

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
                    Bot.getBotInstance().sendServerStoppedMessage(getServerName());

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
