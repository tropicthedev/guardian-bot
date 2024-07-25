package com.tropicoss.guardian.minecraft.callback;

import com.google.gson.Gson;
import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.networking.messaging.LoginMessage;
import com.tropicoss.guardian.networking.messaging.LogoutMessage;
import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import static com.tropicoss.guardian.Guardian.*;

public class ServerPlayerConnectionCallback extends ServerEventCallback implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {

    private final Gson gson = new Gson();

    public ServerPlayerConnectionCallback(String host, String serverName, String mode, int port) {
        super(host, serverName, mode, port);
    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        LogoutMessage logoutMessage = new LogoutMessage(getServerName(), handler.player.getUuidAsString());

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(handler.player.getUuidAsString());

        String json = gson.toJson(logoutMessage);

        switch(getMode()) {
            case "server" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendLeaveMessage(profile, getServerName());
                }

                SOCKET_SERVER.broadcast(json);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendLeaveMessage(profile, getServerName());
                }
            }
        }
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        LoginMessage loginMessage = new LoginMessage(getServerName(), handler.player.getUuidAsString());

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(handler.player.getUuidAsString());

        String json = gson.toJson(loginMessage);

        switch(getMode()) {
            case "server" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendJoinMessage(profile, getServerName());
                }

                SOCKET_SERVER.broadcast(json);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendJoinMessage(profile, getServerName());
                }
            }
        }
    }
}
