package com.tropicoss.guardian.networking;

import com.tropicoss.guardian.networking.messaging.MessageHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static com.tropicoss.guardian.Guardian.LOGGER;
import static com.tropicoss.guardian.Guardian.MINECRAFT_SERVER;

public class Client extends WebSocketClient {

    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {

        LOGGER.info("Connected To Server");
    }

    @Override
    public void onMessage(String message) {
        MessageHandler messageHandler = new MessageHandler(MINECRAFT_SERVER);

        messageHandler.handleMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.info("Disconnected From Server");
    }

    @Override
    public void onError(Exception ex) {
        LOGGER.error("Error from " + this.getURI().getHost(), ex);
    }

    public void reload() {
        try {
            closeBlocking();
            reconnect();
        } catch (InterruptedException e) {
            LOGGER.error("Error reloading connection: " + e.getMessage());
        }
    }
}