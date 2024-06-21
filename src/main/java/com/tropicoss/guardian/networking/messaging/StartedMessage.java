package com.tropicoss.guardian.networking.messaging;

import net.minecraft.text.Text;

public class StartedMessage implements WebsocketMessage {

    public String origin;

    public Long uptime;

    private final String type = "started";

    public StartedMessage(String origin, Long uptime) {
        this.origin = origin;
        this.uptime = uptime;
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server started in %sS 🕛", this.origin, this.uptime / 1000);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §fServer started in %sS 🕛", this.origin, this.uptime / 1000));
    }
}
