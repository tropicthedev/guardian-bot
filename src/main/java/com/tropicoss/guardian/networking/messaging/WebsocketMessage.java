package com.tropicoss.guardian.networking.messaging;

import net.minecraft.text.Text;

public interface WebsocketMessage {

    public String toConsoleString();

    public Text toChatText();
}
