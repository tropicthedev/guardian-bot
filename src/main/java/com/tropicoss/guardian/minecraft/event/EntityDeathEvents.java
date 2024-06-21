package com.tropicoss.guardian.minecraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface EntityDeathEvents
{

    Event<EntityDeathEvents> EVENT =
            EventFactory.createArrayBacked(EntityDeathEvents.class, callbacks -> (entity, source) -> {
                for (EntityDeathEvents callback : callbacks) {
                    callback.onEntityDeath(entity, source);
                }
            });

    void onEntityDeath(LivingEntity entity, DamageSource source);
}
