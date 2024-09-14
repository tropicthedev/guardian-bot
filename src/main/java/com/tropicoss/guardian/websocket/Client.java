package com.tropicoss.guardian.websocket;

import com.tropicoss.guardian.websocket.message.MessageHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import static com.tropicoss.guardian.Guardian.LOGGER;
import static com.tropicoss.guardian.Guardian.MINECRAFT_SERVER;

public class Client extends WebSocketClient {

    private static final long RETRY_INTERVAL_MS = 60 * 1000; // 1 minute

    private Timer reconnectTimer;

    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        LOGGER.info("Connected To Guardian's Websocket Server");
        // Stop any existing reconnection timer when connected
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
            reconnectTimer = null;
        }
    }

    @Override
    public void onMessage(String message) {
        MessageHandler messageHandler = new MessageHandler(MINECRAFT_SERVER);
        messageHandler.handleMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.info("Disconnected From Guardian's Websocket Server. Reason: {}", reason);
        attemptReconnect();
    }

    @Override
    public void onError(Exception ex) {
        LOGGER.error("Error from {}", this.getURI().getHost(), ex);
        attemptReconnect();
    }

    private void attemptReconnect() {
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }

        reconnectTimer = new Timer(true); // Daemon timer
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    reconnect();
                } catch (Exception e) {
                    LOGGER.error("Error during reconnection attempt: " + e.getMessage());
                }
            }
        }, RETRY_INTERVAL_MS);
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