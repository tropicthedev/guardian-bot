package com.tropicoss.guardian.minecraft.callback;

import com.google.gson.Gson;
import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.minecraft.event.AdvancementEvent;
import com.tropicoss.guardian.networking.messaging.AdvancementMessage;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.tropicoss.guardian.Guardian.*;


public class AdvancementCallback implements AdvancementEvent {
    @Override
    public void onGrantCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) {
        AdvancementDisplay advancementDisplay = advancement.value().display().get();

        if(!advancementDisplay.shouldAnnounceToChat()) return;

        AdvancementMessage advancementMessage = new AdvancementMessage(advancementDisplay.getTitle().getString(), advancementDisplay.getDescription().getString(), player.getUuidAsString());

        String json = new Gson().toJson(advancementMessage);

        switch (CONFIG_MANAGER.getSetting("generic", "mode")){
            case "server" -> {
                SOCKET_SERVER.broadcast(json);

                Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" -> Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
        }
    }
}