package com.tropicoss.guardian.networking.messaging;

import net.minecraft.text.Text;

public class StoppedMessage implements WebsocketMessage {

    private final String type = "stopped";
    public String server;

    public StoppedMessage(String server) {
        this.server = server;
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server Stopping", this.server);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §fServer Stopping", this.server));
    }
}
