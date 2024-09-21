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
    public boolean execute(GameProfile gameProfile, MinecraftServer server) {

        try {
            BannedPlayerList bannedPlayerList = server.getPlayerManager().getUserBanList();

            if (bannedPlayerList.contains(gameProfile)) {

                LOGGER.info("{} is already banned", gameProfile.getName());

                return false;
            }

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

            LOGGER.info("{} has been banned ", gameProfile.getName());

            return true;

        } catch (Exception e) {

            LOGGER.error("{} Could Not Be Banned From The Server: {}", gameProfile.getName(), e.getMessage());

        }

        return false;
    }
}
