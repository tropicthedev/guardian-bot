package com.tropicoss.guardian.discord.events;

import com.google.gson.Gson;
import com.tropicoss.guardian.networking.messaging.DiscordMessage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.tropicoss.guardian.Guardian.*;

public class ChatAdapter extends ListenerAdapter {
    private final boolean SERVER = Objects.equals(CONFIG_MANAGER.getSetting("generic", "mode"), "server");

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            if (event.getAuthor().isBot()) return;

            if (!event.getChannel().getId().equals(CONFIG_MANAGER.getSetting("bot", "chatChannel"))) return;

            String member = Objects.requireNonNull(event.getGuild().getMember(event.getAuthor()))
                    .getEffectiveName();

            DiscordMessage msg = new DiscordMessage(event.getMessage().getContentRaw(), member);

            LOGGER.info(msg.toConsoleString());

            // Send message to all players. Broadcast adds the colors to the console sadly.
            MINECRAFT_SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(msg.toChatText(), false));

            String json =  new Gson().toJson(msg);

            if (SERVER) {
                SOCKET_SERVER.broadcast(json);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}