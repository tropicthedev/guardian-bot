package com.tropicoss.guardian.minecraft.callback;

import com.google.gson.Gson;
import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.networking.messaging.ChatMessage;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.tropicoss.guardian.Guardian.*;

public class ServerMessageCallback  extends ServerEventCallback implements ServerMessageEvents.ChatMessage, ServerMessageEvents.CommandMessage{

    public ServerMessageCallback(String host, String serverName, String mode, int port) {
        super(host, serverName, mode, port);
    }

    @Override
    public void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {

       try {
           ChatMessage msg = new ChatMessage(getServerName(), sender.getUuid().toString(), message.getContent().getString());

           String json = new Gson().toJson(msg);

           switch (getMode()) {
               case "server" -> {
                   Bot.getBotInstance().sendWebhook(message.getContent().getString(), msg.getProfile(),getServerName());

                   SOCKET_SERVER.broadcast(json);
               }

               case "standalone" -> Bot.getBotInstance().sendWebhook(message.getContent().getString(), msg.getProfile(), getServerName());

               case "client" -> SOCKET_CLIENT.send(json);
           }
       } catch (Exception e) {
           LOGGER.error(e.getMessage());
       }
    }

    @Override
    public void onCommandMessage(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {
        final String typeKey = params.type().value().chat().translationKey();

        switch (typeKey) {
            case "chat.type.emote" -> handleMeCommand(message, source, params);

            case "chat.type.announcement" -> handleSayCommand(message, source, params);
        }
    }

    private void handleSayCommand(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {

    }

    private void handleMeCommand(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {

    }
}
