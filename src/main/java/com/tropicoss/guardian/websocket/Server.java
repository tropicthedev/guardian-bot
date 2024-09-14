package com.tropicoss.guardian.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.websocket.message.MessageHandler;
import net.fabricmc.loader.api.FabricLoader;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.util.*;

import static com.tropicoss.guardian.Guardian.LOGGER;
import static com.tropicoss.guardian.Guardian.MINECRAFT_SERVER;

public class Server extends WebSocketServer {

    private final Set<WebSocket> connections = Collections.synchronizedSet(new HashSet<>());

    private final Config config = Config.getInstance();

    public Server(InetSocketAddress address) throws FileNotFoundException {
        super(address);
        String filePath = FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.json").toString();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
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

        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        if (jsonObject.has("type")) {

            String messageType = jsonObject.get("type").getAsString();

            if(Objects.equals(messageType, "command")) {
                broadcastToAllExcept(conn, message);
            }
        }
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

    public void broadcastToAllExcept(WebSocket sender, String message) {
        synchronized (connections) {
            for (WebSocket conn : connections) {
                if (!conn.equals(sender)) {
                    conn.send(message);
                }
            }
        }
    }
}