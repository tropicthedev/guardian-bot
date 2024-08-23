package com.tropicoss.guardian.networking.messaging;

import net.minecraft.text.Text;

public class EntityDeathMessage implements WebsocketMessage {

    public final String origin;
    private final String type = "death";
    public String message;
    public String coordinates;

    public EntityDeathMessage(String message, String coordinates, String origin) {
        this.message = message;
        this.coordinates = coordinates;
        this.origin = origin;
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] [%s] %s ", this.origin, this.coordinates, this.message);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §b[%s] §f%s", this.origin, this.coordinates, this.message));
    }
}
