package com.tropicoss.guardian.minecraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.FileNotFoundException;

@FunctionalInterface
public interface AdvancementEvent {

    Event<AdvancementEvent> EVENT =
            EventFactory.createArrayBacked(AdvancementEvent.class, callbacks -> (player, advancement, criterion) -> {
                for (AdvancementEvent callback : callbacks) {
                    callback.onGrantCriterion(player, advancement, criterion);
                }
            });

    void onGrantCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) throws FileNotFoundException;
}
