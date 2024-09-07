package com.tropicoss.guardian.javalin.websocket;

import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.javalin.websocket.message.MessageHandler;
import net.fabricmc.loader.api.FabricLoader;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.FileNotFoundException;
import java.net.InetSocketAddress;

import static com.tropicoss.guardian.Guardian.LOGGER;
import static com.tropicoss.guardian.Guardian.MINECRAFT_SERVER;

public class Server extends WebSocketServer {

    private final Config config = Config.getInstance();

    public Server(InetSocketAddress address) throws FileNotFoundException {
        super(address);
        String filePath = FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.json").toString();
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
        MessageHandler messageHandler = new MessageHandler(MINECRAFT_SERVER);

        messageHandler.handleMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.error("Error from {}", conn.getRemoteSocketAddress().getAddress().getHostAddress(), ex);
    }

    @Override
    public void onStart() {
        LOGGER.info("Socket Server Started");
        LOGGER.info("Listening on port :{}", config.getConfig().getServer().getPort());
    }
}