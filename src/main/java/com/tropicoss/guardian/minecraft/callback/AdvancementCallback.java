package com.tropicoss.guardian.minecraft.callback;

import com.google.gson.Gson;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.minecraft.event.AdvancementEvent;
import com.tropicoss.guardian.networking.messaging.AdvancementMessage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.FileNotFoundException;

import static com.tropicoss.guardian.Guardian.*;


public class AdvancementCallback implements AdvancementEvent {

    private final Config config = Config.getInstance();
    public AdvancementCallback() throws FileNotFoundException {
        String filePath = FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.json").toString();
    }

    @Override
    public void onGrantCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) throws FileNotFoundException {
        AdvancementDisplay advancementDisplay = advancement.value().display().get();

        if(!advancementDisplay.shouldAnnounceToChat()) return;

        AdvancementMessage advancementMessage = new AdvancementMessage(advancementDisplay.getTitle().getString(),
                advancementDisplay.getDescription().getString(), player.getUuidAsString(),
                this.config.getConfig().getGeneric().getName());

        String json = new Gson().toJson(advancementMessage);

        switch (config.getConfig().getGeneric().getName()){
            case "server" -> {
                SOCKET_SERVER.broadcast(json);

                Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" -> Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
        }
    }
}