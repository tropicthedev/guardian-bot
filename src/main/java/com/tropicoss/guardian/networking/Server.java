package com.tropicoss.guardian.networking;

import com.tropicoss.guardian.Guardian;
import com.tropicoss.guardian.networking.messaging.MessageHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import static com.tropicoss.guardian.Guardian.CONFIG_MANAGER;
import static com.tropicoss.guardian.Guardian.LOGGER;

public class Server extends WebSocketServer {

    public Server(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info(
                "New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LOGGER.info(
                "Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        MessageHandler messageHandler = new MessageHandler();

        messageHandler.handleMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.error("Error from " + conn.getRemoteSocketAddress().getAddress().getHostAddress(), ex);
    }

    @Override
    public void onStart() {
        LOGGER.info("Socket Server Started");
        LOGGER.info("Listening on port " + CONFIG_MANAGER.getSetting("server", "port"));
    }
}