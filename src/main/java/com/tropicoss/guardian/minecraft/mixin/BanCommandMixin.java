package com.tropicoss.guardian.minecraft.mixin;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.tropicoss.guardian.services.websocket.message.CommandMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

import static com.tropicoss.guardian.Mod.SOCKET_CLIENT;
import static com.tropicoss.guardian.Mod.SOCKET_SERVER;

@Mixin(BanCommand.class)
public abstract class BanCommandMixin {

    @Inject(method = "ban", at = @At("TAIL"))
    private static void ban(ServerCommandSource source, Collection<GameProfile> targets, @Nullable Text reason, CallbackInfoReturnable<Integer> cir) {

        for (GameProfile gameProfile : targets) {
            CommandMessage commandMessage = new CommandMessage(gameProfile.getId().toString(), gameProfile.getName(), "add");

            String json = new Gson().toJson(commandMessage);

            if (SOCKET_CLIENT != null) {
                if (SOCKET_CLIENT.isOpen()) {
                    SOCKET_CLIENT.send(json);
                }
            }

            if (SOCKET_SERVER != null) {
                SOCKET_SERVER.broadcast(json);
            }
        }
    }
}
