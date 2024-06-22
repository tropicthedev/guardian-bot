package com.tropicoss.guardian.networking.messaging;

import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.discord.Bot;
import net.minecraft.server.MinecraftServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.util.UUID;

public class MessageHandlerTest {

    @BeforeEach
    public void setup() {

    }

    // Should send player login message to all active minecraft players
    public void shouldSendLoginToAllPlayers() {

    }

    // Should send player login message to discord if it is a server
    public void shouldSendLoginMessageToDiscord(){

    }

    // Should not send player login message to discord if it is not a server
    @Test
    public void shouldNotSendLoginMessageToDiscord() throws InterruptedException, FileNotFoundException {


        LoginMessage message = new LoginMessage("server", UUID.randomUUID().toString());

        // In the config manager set the config option that indicates that's a server
        ConfigurationManager configurationManager = new ConfigurationManager(String.valueOf(ClassLoader.getSystemResourceAsStream("config.json")));
        // instantiate a message handler object
        MessageHandler messageHandler = new MessageHandler(configurationManager);

        // Create a mock bot object
        Bot bot =  Mockito.mock(Bot.class);

        messageHandler.setBot(bot);

        // instantiate the minecraft server in guardian
        MinecraftServer minecraftServer = Mockito.mock(MinecraftServer.class);
        // Call the Message Handler (LoginMessage)
        messageHandler.handleLoginMessage(message);
    }
}
