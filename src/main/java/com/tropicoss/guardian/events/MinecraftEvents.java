package com.tropicoss.guardian.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MinecraftEvents {
    public static final Event<PlayerChat> PLAYER_CHAT_EVENT = EventFactory.createArrayBacked(PlayerChat.class, (listeners) ->
            (server, text, sender) -> {
                for (PlayerChat listener : listeners) {
                    listener.onPlayerChat(server, text, sender);
                }
            });

    public static final Event<ServerChat> SERVER_CHAT_EVENT =
                    EventFactory.createArrayBacked(
                            ServerChat.class,
                            (listeners) ->
                                    (server, text) -> {
                                        for (ServerChat listener : listeners) {
                                            listener.onServerChat(server, text);
                                        }
                                    });

    @FunctionalInterface
    public interface PlayerChat {
        void onPlayerChat(MinecraftServer server, Text text, ServerPlayerEntity sender);
    }

    @FunctionalInterface
    public interface ServerChat {
        void onServerChat(MinecraftServer server, Text text);
    }
}
