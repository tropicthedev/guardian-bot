package com.tropicoss.guardian.minecraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.FileNotFoundException;

@FunctionalInterface
public interface AdvancementCallback {

    Event<AdvancementCallback> EVENT =
            EventFactory.createArrayBacked(AdvancementCallback.class, callbacks -> (player, advancement, criterion) -> {
                for (AdvancementCallback callback : callbacks) {
                    callback.onGrantCriterion(player, advancement, criterion);
                }
            });

    void onGrantCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) throws FileNotFoundException;
}
