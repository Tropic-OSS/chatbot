package com.tropicoss.guardian.minecraft;

import static net.minecraft.server.command.CommandManager.literal;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

public class Commands {
  public static void register() {
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) ->
            dispatcher.register(
                literal("guardian")
                    .executes(
                        context -> {
                          context.getSource().sendFeedback(() -> Text.literal("Hello"), false);
                          return 1;
                        })));
  }
}
