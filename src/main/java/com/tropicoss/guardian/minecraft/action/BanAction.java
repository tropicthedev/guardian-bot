package com.tropicoss.guardian.minecraft.action;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class BanAction implements Action{
    @Override
    public void execute(GameProfile gameProfile, MinecraftServer server) {

        try {
            BannedPlayerList bannedPlayerList = server.getPlayerManager().getUserBanList();

            if (bannedPlayerList.contains(gameProfile)) {
                String msg = gameProfile.getName() + " is already banned";

//                SocketClient.getInstance(server).emitSuccessEvent(msg, false);

                LOGGER.info(gameProfile.getName() + " is already banned");
            } else {

                BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(
                        gameProfile,
                        null,
                        null,
                        null,
                        null
                );

                bannedPlayerList.add(bannedPlayerEntry);

                ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.getId());

                if (serverPlayerEntity != null) {

                    serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.banned"));

                }

                String msg = gameProfile.getName() + " Has been Banned From The Server";

//                SocketClient.getInstance(server).emitSuccessEvent(msg, true);

                LOGGER.info(gameProfile.getName() + " has been banned ");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());

            String msg = gameProfile.getName() + " Could Not Be Banned From The Server";

//            SocketClient.getInstance(server).emitSuccessEvent(msg, false);
        }
    }
}
