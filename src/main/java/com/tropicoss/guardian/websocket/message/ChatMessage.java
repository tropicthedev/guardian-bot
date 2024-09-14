package com.tropicoss.guardian.websocket.message;

import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import net.minecraft.text.Text;

public class ChatMessage implements Message {

    private final String type = "chat";
    public String origin;
    public String uuid;
    public String content;

    public ChatMessage(String origin, String uuid, String content) {
        this.origin = origin;
        this.uuid = uuid;
        this.content = content;
    }

    public PlayerInfoFetcher.Profile getProfile() {
        return PlayerInfoFetcher.getProfile(this.uuid);
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] %s: %s", this.origin, this.getProfile().data.player.username,
                this.content);
    }

    @Override
    public Text toChatText() {

        return Text.of(String.format("§9[%s] §b%s: §f%s", this.origin, this.getProfile().data.player.username,
                this.content));
    }
}


