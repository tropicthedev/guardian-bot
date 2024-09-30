package com.tropicoss.guardian.minecraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface PlayerDeathCallback {

    Event<PlayerDeathCallback> EVENT =
            EventFactory.createArrayBacked(PlayerDeathCallback.class, callbacks -> (player, source) -> {
                for (PlayerDeathCallback callback : callbacks) {
                    callback.onPlayerDeath(player, source);
                }
            });

    void onPlayerDeath(ServerPlayerEntity player, DamageSource source);
}
