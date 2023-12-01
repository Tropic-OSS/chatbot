package com.tropicoss.alfred.events;

import net.dv8tion.jda.api.entities.Message;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class DiscordEvents {

    public static final Event<DiscordChat> DISCORD_CHAT_EVENT =
            EventFactory.createArrayBacked(
                    DiscordChat.class,
                    (listeners) ->
                            (message) -> {
                                for (DiscordChat listener : listeners) {
                                    listener.onDiscordChat(message);
                                }
                            });

    @FunctionalInterface
    public interface DiscordChat {

        void onDiscordChat(Message message);
    }
}
