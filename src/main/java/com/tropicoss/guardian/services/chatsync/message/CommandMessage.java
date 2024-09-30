package com.tropicoss.guardian.services.chatsync.message;

import net.minecraft.text.Text;

public class CommandMessage implements Message {

    private final String type = "command";
    public String uuid;
    public String name;
    public String action;

    public CommandMessage(String uuid, String name, String action) {
        this.uuid = uuid;
        this.name = name;
        this.action = action;
    }


    @Override
    public String toConsoleString() {
        return "";
    }

    @Override
    public Text toChatText() {
        return null;
    }
}
