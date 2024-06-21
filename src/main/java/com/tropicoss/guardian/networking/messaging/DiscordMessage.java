package com.tropicoss.guardian.networking.messaging;

import net.minecraft.text.Text;

public class DiscordMessage implements WebsocketMessage {

    public String message;

    public String member;

    private final String type = "discord";

    public DiscordMessage(String message, String member) {
        this.message = message;
        this.member = member;
    }

    @Override
    public String toConsoleString() {
        return String.format("[Discord] %s: %s", this.member, this.message);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[Discord] §b%s: §f%s", this.member,
                this.message));
    }
}
