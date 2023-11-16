package com.tropicoss.minecraft.alfred.bot.adapters;

import com.tropicoss.minecraft.alfred.config.Config;
import com.tropicoss.minecraft.alfred.events.EventHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessagesAdapter extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (!event.getChannel().getId().equals(Config.Bot.channel)) return;

        EventHandler eventHandler = new EventHandler();

        eventHandler.onDiscordChat(event.getMessage());
    }
}
