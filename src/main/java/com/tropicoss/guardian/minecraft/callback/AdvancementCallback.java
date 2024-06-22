package com.tropicoss.guardian.minecraft.callback;

import com.google.gson.Gson;
import com.tropicoss.guardian.config.ConfigurationManager;
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

    private final ConfigurationManager configurationManger;

    public AdvancementCallback() throws FileNotFoundException {
        String filePath = FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.json").toString();
        this.configurationManger = new ConfigurationManager(filePath);
    }

    @Override
    public void onGrantCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) throws FileNotFoundException {
        AdvancementDisplay advancementDisplay = advancement.value().display().get();

        if(!advancementDisplay.shouldAnnounceToChat()) return;

        AdvancementMessage advancementMessage = new AdvancementMessage(advancementDisplay.getTitle().getString(),
                advancementDisplay.getDescription().getString(), player.getUuidAsString(),
                this.configurationManger.getSetting("generic", "serverName"));

        String json = new Gson().toJson(advancementMessage);

        switch (configurationManger.getSetting("generic", "mode")){
            case "server" -> {
                SOCKET_SERVER.broadcast(json);

                Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" -> Bot.getBotInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
        }
    }
}