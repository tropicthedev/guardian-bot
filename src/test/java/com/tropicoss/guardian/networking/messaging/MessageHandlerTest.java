package com.tropicoss.guardian.networking.messaging;

import com.mojang.authlib.GameProfile;
import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.mockito.Mockito.*;

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

        Logger logger = mock(Logger.class);
        // Uses Notch for Testing
        LoginMessage message = new LoginMessage("server", "069a79f4-44e9-4726-a5be-fca90e38aaf5");

        // In the config manager set the config option that indicates that's a server
        InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream("config.json");
        InputStreamReader streamReader = new InputStreamReader(resourceAsStream);

        // instantiate the minecraft server in guardian
        MinecraftServer minecraftServer = mock(MinecraftServer.class);

        ConfigurationManager configurationManager = new ConfigurationManager(streamReader);
        configurationManager.loadConfig();

        // instantiate a message handler object
        MessageHandler messageHandler = new MessageHandler(configurationManager, minecraftServer);

        // Create a mock bot object
        Bot bot =  mock(Bot.class);

        messageHandler.setBot(bot);

        ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(minecraftServer, mock(ServerWorld.class), mock(GameProfile.class), null);
        List<ServerPlayerEntity> serverPlayerEntityList = Collections.singletonList(serverPlayerEntity);;

        PlayerManager playerManager = mock(PlayerManager.class);

        when(minecraftServer.getPlayerManager()).thenReturn(playerManager);
        when(minecraftServer.getPlayerManager().getPlayerList()).thenReturn(serverPlayerEntityList);

        when(message.toConsoleString()).thenReturn("Login Message");

        // Call the Message Handler (LoginMessage)
        messageHandler.handleLoginMessage(message);

        verify(logger).info("Login Message");
        verify(serverPlayerEntity).sendMessage(any(Text.class), eq(false));
        verify(bot).sendJoinMessage(any(PlayerInfoFetcher.Profile.class), anyString());
    }
}
