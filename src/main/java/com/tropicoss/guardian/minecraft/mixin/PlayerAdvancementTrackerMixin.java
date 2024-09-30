package com.tropicoss.guardian.minecraft.mixin;

import com.tropicoss.guardian.minecraft.event.AdvancementCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.FileNotFoundException;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/advancement/"
            + "AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void grantCriterion(AdvancementEntry advancementEntry, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        Advancement advancement = advancementEntry.value();

        // Use ifPresent to safely access the value of the Optional
        advancement.display().ifPresent(display -> {
            if (display.shouldAnnounceToChat()) {
                try {
                    AdvancementCallback.EVENT.invoker().onGrantCriterion(owner, advancementEntry, criterionName);
                } catch (FileNotFoundException e) {
                    LOGGER.error("An exception has been thrown  {}", e.getMessage());
                }
            }
        });
    }
}
