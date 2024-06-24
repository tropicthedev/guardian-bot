package com.tropicoss.guardian.networking;

import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.networking.messaging.MessageHandler;
import com.tropicoss.guardian.services.MinecraftServerService;
import net.fabricmc.loader.api.FabricLoader;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.FileNotFoundException;
import java.net.URI;

import static com.tropicoss.guardian.Guardian.LOGGER;

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
        try {
            ConfigurationManager configurationManager = new ConfigurationManager(FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.json").toString());

            MessageHandler messageHandler = new MessageHandler(configurationManager, MinecraftServerService.getServerInstance());

            messageHandler.handleMessage(message);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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