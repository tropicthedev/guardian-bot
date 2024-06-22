package com.tropicoss.guardian.networking;

import com.tropicoss.guardian.Guardian;
import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.networking.messaging.MessageHandler;
import net.fabricmc.loader.api.FabricLoader;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.InetSocketAddress;

import static com.tropicoss.guardian.Guardian.CONFIG_MANAGER;

public class Server extends WebSocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public Server(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info("New connection from {}", conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LOGGER.info("Closed connection to {}", conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            ConfigurationManager configurationManager = new ConfigurationManager(FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.json").toString());

            MessageHandler messageHandler = new MessageHandler(configurationManager);

            messageHandler.handleMessage(message);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.error("Error from {}", conn.getRemoteSocketAddress().getAddress().getHostAddress(), ex);
    }

    @Override
    public void onStart() {
        LOGGER.info("Socket Server Started");
        LOGGER.info("Listening on port {}", CONFIG_MANAGER.getSetting("server", "port"));
    }
}