package com.tropicoss.guardian.javalin.websocket.message;

import net.minecraft.text.Text;

public interface Message {

    String toConsoleString();

    Text toChatText();
}
