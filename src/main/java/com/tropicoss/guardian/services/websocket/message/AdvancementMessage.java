package com.tropicoss.guardian.services.websocket.message;

import com.tropicoss.guardian.services.PlayerInfoFetcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class AdvancementMessage implements Message {

    public final String origin;
    private final String type = "advancement";
    public String title;
    public String description;
    public String uuid;

    public AdvancementMessage(String title, String description, String uuid, String origin) {
        this.title = title;
        this.description = description;
        this.uuid = uuid;
        String filePath = FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.json").toString();
        this.origin = origin;
    }

    public PlayerInfoFetcher.Profile getProfile() {
        return PlayerInfoFetcher.getProfile(this.uuid);
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] [%s] %s got an achievement: %s ", this.origin, this.title, getProfile().data.player.username, this.description);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §b[%s] §f%s %s", this.origin, this.title, getProfile().data.player.username, this.description));

    }
}
