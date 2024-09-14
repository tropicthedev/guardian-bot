package com.tropicoss.guardian.minecraft.action;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;

public interface Action {

    void execute(GameProfile gameProfile, MinecraftServer server);
}
