package com.tropicoss.guardian.websocket.message;

import net.minecraft.text.Text;

public interface Message {

    String toConsoleString();

    Text toChatText();
}
