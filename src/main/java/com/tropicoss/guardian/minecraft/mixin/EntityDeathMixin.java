package com.tropicoss.guardian.minecraft.mixin;

import com.tropicoss.guardian.minecraft.event.EntityDeathCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityDeathMixin {

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus("
            + "Lnet/minecraft/entity/Entity;B)V"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        EntityDeathCallback.EVENT.invoker().onEntityDeath((LivingEntity) (Object) this, source);
    }
}
