package com.tropicoss.guardian.minecraft.mixin;

import com.tropicoss.guardian.minecraft.event.PlayerDeathCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerDeathMixin {
    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus("
            + "Lnet/minecraft/entity/Entity;B)V"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        PlayerDeathCallback.EVENT.invoker().onPlayerDeath((ServerPlayerEntity) (Object) this, source);
    }
}
