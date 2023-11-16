package com.tropicoss.minecraft.alfred.minecraft;

import com.tropicoss.minecraft.alfred.Alfred;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(
                                literal("alfred")
                                        .executes(
                                                context -> {
                                                    context.getSource().sendFeedback(() -> Text.literal("Hello"), false);
                                                    return 1;
                                                })
                                        .then(
                                                literal("reload")
                                                        .executes(
                                                                context -> {
                                                                    context
                                                                            .getSource()
                                                                            .sendFeedback(() -> Text.literal("Reloading"), false);
                                                                    Alfred.SOCKET_CLIENT.reload();
                                                                    return 1;
                                                                }))));
    }
}
