package com.tropicoss.guardian.networking.messaging;

import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import net.minecraft.text.Text;

public class LogoutMessage implements WebsocketMessage {
    private final String type = "logout";
    public String origin;
    public String uuid;

    public LogoutMessage(String origin, String uuid) {
        this.origin = origin;
        this.uuid = uuid;
    }

    public PlayerInfoFetcher.Profile getProfile() {
        return PlayerInfoFetcher.getProfile(this.uuid);
    }

    @Override
    public String toConsoleString() {

        return String.format("[%s] %s left the server", this.origin, this.getProfile().data.player.username);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §%s: §fleft the server", this.origin, this.getProfile().data.player.username));
    }
}
