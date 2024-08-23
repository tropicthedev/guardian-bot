package com.tropicoss.guardian.minecraft.mixin;

import com.tropicoss.guardian.minecraft.event.AdvancementEvent;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.FileNotFoundException;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/"
            + "AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void grantCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) throws FileNotFoundException {
        AdvancementEvent.EVENT.invoker().onGrantCriterion(owner, advancement, criterionName);
    }
}