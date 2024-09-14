package com.tropicoss.guardian.websocket.message;

import net.minecraft.text.Text;

public class StartedMessage implements Message {

    private final String type = "started";
    public String origin;
    public Long uptime;

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
