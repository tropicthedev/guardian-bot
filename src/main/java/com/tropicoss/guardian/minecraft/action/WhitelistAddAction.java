package com.tropicoss.guardian.minecraft.action;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class WhitelistAddAction implements Action {

    @Override
    public boolean execute(GameProfile gameProfile, MinecraftServer server) {

        try {
            Whitelist whitelist = server.getPlayerManager().getWhitelist();

            if(whitelist.isAllowed(gameProfile)) {
                LOGGER.error("Member {} could not be added to the whitelist", gameProfile.getName());

                return false;
            }

            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);

            whitelist.add(whitelistEntry);

            LOGGER.info("Member {} has been added to the whitelist", gameProfile.getName());

            return true;

        } catch (Exception e) {

            LOGGER.error("{} could not be added to the whitelist: {}", gameProfile.getName(), e.getMessage());
        }

        return false;
    }
}