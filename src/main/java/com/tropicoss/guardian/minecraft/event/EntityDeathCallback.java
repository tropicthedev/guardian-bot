package com.tropicoss.guardian.minecraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@FunctionalInterface
public interface EntityDeathCallback {

    Event<EntityDeathCallback> EVENT =
            EventFactory.createArrayBacked(EntityDeathCallback.class, callbacks -> (entity, source) -> {
                for (EntityDeathCallback callback : callbacks) {
                    callback.onEntityDeath(entity, source);
                }
            });

    void onEntityDeath(LivingEntity entity, DamageSource source);
}
