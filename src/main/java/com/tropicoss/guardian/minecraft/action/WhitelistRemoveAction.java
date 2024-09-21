package com.tropicoss.guardian.minecraft.action;


import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class WhitelistRemoveAction implements Action {

    @Override
    public boolean execute(GameProfile gameProfile, MinecraftServer server) {

        Whitelist whitelist = server.getPlayerManager().getWhitelist();

        try {
            if (!whitelist.isAllowed(gameProfile)) {
                LOGGER.error("Member {} could not be removed from the whitelist, not allowed", gameProfile.getName());
                return false;
            }

            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            whitelist.remove(whitelistEntry);

            LOGGER.info("Member {} has been removed from the whitelist", gameProfile.getName());
            return true;

        } catch (Exception e) {
            LOGGER.error("An error occurred while removing {} from the whitelist: {}", gameProfile.getName(), e.getMessage());
        }

        return false;
    }
}