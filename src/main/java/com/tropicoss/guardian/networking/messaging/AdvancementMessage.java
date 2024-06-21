package com.tropicoss.guardian.networking.messaging;

import com.tropicoss.guardian.Guardian;
import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import net.minecraft.text.Text;

import static com.tropicoss.guardian.Guardian.CONFIG_MANAGER;

public class AdvancementMessage implements WebsocketMessage{

    private final String type = "advancement";
    public String title;

    public String description;

    public String uuid;

    public final String origin = CONFIG_MANAGER.getSetting("generic", "serverName");

    public AdvancementMessage(String title, String description, String uuid) {
        this.title = title;
        this.description = description;
        this.uuid = uuid;
    }

    public PlayerInfoFetcher.Profile getProfile() {
        return PlayerInfoFetcher.getProfile(this.uuid);
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] [%s] %s got an achievement: %s ", this.origin, this.title, getProfile().data.player.username , this.description);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §b[%s] §f%s %s", this.origin, this.title, getProfile().data.player.username, this.description));

    }
}
