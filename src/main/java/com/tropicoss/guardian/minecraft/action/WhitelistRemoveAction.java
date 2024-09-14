package com.tropicoss.guardian.minecraft.action;


import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class WhitelistRemoveAction implements Action {

    @Override
    public void execute(GameProfile gameProfile, MinecraftServer server) {

        try {
            Whitelist whitelist = server.getPlayerManager().getWhitelist();

            if (whitelist.isAllowed(gameProfile)) {

                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);

                whitelist.remove(whitelistEntry);

                String msg = gameProfile.getName() + " has been removed from the whitelist";

//                SocketClient.getInstance(server).emitSuccessEvent(msg, true);

                LOGGER.info("Member " + gameProfile.getName() + " has been removed from the whitelist");

            } else {
                String msg = gameProfile.getName() + " could not be removed from the whitelist";

//                SocketClient.getInstance(server).emitSuccessEvent(msg, false);

                LOGGER.error("Member " + gameProfile.getName() + " could not be removed from the whitelist");
            }

        } catch (Exception e) {
            String msg = gameProfile.getName() + " could not be removed from the whitelist";

//            SocketClient.getInstance(server).emitSuccessEvent(msg, false);
            LOGGER.error(e.getMessage());
        }
    }
}