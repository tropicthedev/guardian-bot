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

public class ServerPlayerConnectionCallback implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {

    private final Gson gson = new Gson();

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        LogoutMessage logoutMessage = new LogoutMessage(CONFIG_MANAGER.getSetting("generic", "serverNane"), handler.player.getUuidAsString());

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(handler.player.getUuidAsString());

        String json = gson.toJson(logoutMessage);

        switch(CONFIG_MANAGER.getSetting("generic", "mode")) {
            case "server" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendLeaveMessage(profile, CONFIG_MANAGER.getSetting("generic", "serverNane"));
                }

                SOCKET_SERVER.broadcast(json);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendLeaveMessage(profile, CONFIG_MANAGER.getSetting("generic", "serverNane"));
                }
            }
        }
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        LoginMessage loginMessage = new LoginMessage(CONFIG_MANAGER.getSetting("generic", "serverNane"), handler.player.getUuidAsString());

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(handler.player.getUuidAsString());

        String json = gson.toJson(loginMessage);

        switch(CONFIG_MANAGER.getSetting("generic", "mode")) {
            case "server" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendJoinMessage(profile, CONFIG_MANAGER.getSetting("generic", "serverNane"));
                }

                SOCKET_SERVER.broadcast(json);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" -> {
                if (profile != null) {
                    Bot.getBotInstance().sendJoinMessage(profile, CONFIG_MANAGER.getSetting("generic", "serverNane"));
                }
            }
        }
    }
}
