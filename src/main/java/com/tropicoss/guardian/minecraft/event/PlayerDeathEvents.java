package com.tropicoss.guardian.minecraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface PlayerDeathEvents {

    Event<PlayerDeathEvents> EVENT =
            EventFactory.createArrayBacked(PlayerDeathEvents.class, callbacks -> (player, source) -> {
                for (PlayerDeathEvents callback : callbacks) {
                    callback.onPlayerDeath(player, source);
                }
            });

    void onPlayerDeath(ServerPlayerEntity player, DamageSource source);
}
