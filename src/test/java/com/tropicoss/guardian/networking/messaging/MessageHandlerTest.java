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
        // Uses Notch for Testing
        LoginMessage message = new LoginMessage("server", "069a79f4-44e9-4726-a5be-fca90e38aaf5");

        // In the config manager set the config option that indicates that's a server
        InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream("config.json");
        InputStreamReader streamReader = new InputStreamReader(resourceAsStream);

        // instantiate the minecraft server in guardian
        MinecraftServer minecraftServer = Mockito.mock(MinecraftServer.class);

        ConfigurationManager configurationManager = new ConfigurationManager(streamReader);
        configurationManager.loadConfig();

        // instantiate a message handler object
        MessageHandler messageHandler = new MessageHandler(configurationManager, minecraftServer);

        // Create a mock bot object
        Bot bot =  Mockito.mock(Bot.class);

        messageHandler.setBot(bot);

        // Call the Message Handler (LoginMessage)
        messageHandler.handleLoginMessage(message);
    }
}
