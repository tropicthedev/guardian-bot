package com.tropicoss.guardian.services.websocket.message;

import net.minecraft.text.Text;

public class StartingMessage implements Message {

    private final String type = "starting";
    public String origin;

    public StartingMessage(String origin) {
        this.origin = origin;
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server Starting...", this.origin);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §fServer Starting...", this.origin));
    }
}
