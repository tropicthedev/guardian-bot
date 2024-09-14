package com.tropicoss.guardian.minecraft.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static com.tropicoss.guardian.Guardian.SOCKET_CLIENT;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(
                                literal("guardian")
                                        .executes(
                                                context -> {
                                                    context.getSource().sendFeedback(() -> Text.literal("To reload Guardian do /guardian reload"), false);
                                                    return 1;
                                                })
                                        .then(
                                                literal("reload")
                                                        .executes(
                                                                context -> {
                                                                    context
                                                                            .getSource()
                                                                            .sendFeedback(() -> Text.literal("Reloading"), false);
                                                                    SOCKET_CLIENT.reload();
                                                                    return 1;
                                                                }))));
    }
}
