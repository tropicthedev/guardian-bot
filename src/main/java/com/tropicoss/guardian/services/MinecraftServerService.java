package com.tropicoss.guardian.services;

import net.minecraft.server.MinecraftServer;

public class MinecraftServerService {
    private static MinecraftServer serverInstance;

    private MinecraftServerService() {}

    public static MinecraftServer getServerInstance() {
        return serverInstance;
    }

    public static void setServerInstance(MinecraftServer server) {
        serverInstance = server;
    }
}
