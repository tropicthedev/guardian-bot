package com.tropicoss.guardian.minecraft.callback;

public abstract class ServerEventCallback {

    private final String host;
    private final String serverName;
    private final String mode;
    private String port;

    public ServerEventCallback(String host, String serverName, String mode, String port) {
        // TODO: Make port an int
        // TODO: Add constuctor to Convert string to int
        this.host = host;
        this.serverName = serverName;
        this.mode = mode;
        this.port = port;
    }

    protected String getHost() {
        return this.host;
    }

    protected String getMode() {
        return this.mode;
    }

    protected String getServerName() {
        return this.serverName;
    }

    protected String getPort() {
        return this.port;
    }
}
