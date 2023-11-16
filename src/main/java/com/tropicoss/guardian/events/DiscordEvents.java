package com.tropicoss.guardian.events;

import com.tropicoss.guardian.bot.Bot;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.dv8tion.jda.api.entities.Message;

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
