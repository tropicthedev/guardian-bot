package com.tropicoss.guardian.networking.messaging;

import com.tropicoss.guardian.Guardian;
import net.minecraft.text.Text;

import static com.tropicoss.guardian.Guardian.CONFIG_MANAGER;

public class EntityDeathMessage implements WebsocketMessage{

    private final String type = "death";

    public String message;

    public String coordinates;

    public final String origin = CONFIG_MANAGER.getSetting("generic", "serverName");

    public EntityDeathMessage(String message, String coordinates) {
        this.message = message;
        this.coordinates = coordinates;
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
