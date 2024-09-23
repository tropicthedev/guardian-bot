package com.tropicoss.guardian.services.discord.events;

import com.google.gson.Gson;
import com.tropicoss.guardian.services.websocket.message.DiscordMessage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.tropicoss.guardian.Mod.LOGGER;
import static com.tropicoss.guardian.Mod.SOCKET_SERVER;

public class ChatAdapter extends ListenerAdapter {

    private final String mode;
    private final String botChannel;
    private final MinecraftServer minecraftServer;

    public ChatAdapter(String mode, String botChannel, MinecraftServer minecraftServer) {
        this.mode = mode;
        this.botChannel = botChannel;
        this.minecraftServer = minecraftServer;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            if (event.getAuthor().isBot()) return;

            if (!event.getChannel().getId().equals(this.botChannel)) return;

            String member = Objects.requireNonNull(event.getGuild().getMember(event.getAuthor()))
                    .getEffectiveName();

            DiscordMessage msg;

            if ((long) event.getMessage().getAttachments().size() > 0) {
                msg = new DiscordMessage("Sent an Attachment: " + event.getMessage().getAttachments().getFirst().getUrl(), member);
            } else {
                msg = new DiscordMessage(event.getMessage().getContentRaw(), member);
            }

            LOGGER.info(msg.toConsoleString());

            minecraftServer.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(msg.toChatText(), false));

            String json = new Gson().toJson(msg);

            if (isServer()) {
                SOCKET_SERVER.broadcast(json);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private boolean isServer() {
        return Objects.equals(this.mode, "server");
    }
}