package com.tropicoss.guardian.services.chatsync.message;

import net.minecraft.text.Text;

public interface Message {

    String toConsoleString();

    Text toChatText();
}
