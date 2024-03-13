package com.tropicoss.alfred.minecraft;

import com.tropicoss.alfred.Alfred;
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
                                                    context.getSource().sendFeedback(() -> Text.literal("To reload Alfred do /alfred reload"), false);
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
        LinktoDiscord();
    }


    public static void LinktoDiscord(){
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(
                                literal("alfred")
                                        .executes(
                                                context -> {
                                                    context.getSource().sendFeedback(() -> Text.literal("To reload Alfred do /alfred reload"), false);
                                                    return 1;
                                                })
                                        .then(
                                                literal("link")
                                                        .executes(
                                                                context -> {
                                                                    context
                                                                            .getSource()
                                                                            .sendFeedback(() -> Text.literal("Reloading"), false);

                                                                    return 1;
                                                                }))));


    }

}
