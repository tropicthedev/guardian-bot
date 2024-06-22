package com.tropicoss.guardian.networking.messaging;

import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.discord.Bot;
import net.minecraft.server.MinecraftServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream("config.json");
        InputStreamReader streamReader = new InputStreamReader(resourceAsStream);

        ConfigurationManager configurationManager = new ConfigurationManager(streamReader);
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
