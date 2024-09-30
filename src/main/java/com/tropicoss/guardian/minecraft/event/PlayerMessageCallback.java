package com.tropicoss.guardian.minecraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.component.Component;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

@FunctionalInterface
public interface PlayerMessageCallback {
    Event<PlayerMessageCallback> PLAYER_MESSAGE = EventFactory.createArrayBacked(PlayerMessageCallback.class, callbacks -> (player, message) -> {
        Optional<Component> result = Optional.empty();
        for (PlayerMessageCallback callback : callbacks) {
            result = callback.onMessage(player, message);
        }
        return result;
    });

    Optional<Component> onMessage(ServerPlayerEntity player, String message);
}
