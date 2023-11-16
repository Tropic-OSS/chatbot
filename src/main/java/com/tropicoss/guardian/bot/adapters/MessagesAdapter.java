package com.tropicoss.guardian.bot.adapters;

import com.tropicoss.guardian.bot.Bot;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.events.DiscordEvents;
import com.tropicoss.guardian.events.EventHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static com.tropicoss.guardian.Guardian.SOCKET_SERVER;

public class MessagesAdapter extends ListenerAdapter {
  private final Bot bot = Bot.getInstance();
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) return;

    if (!event.getChannel().getId().equals(Config.Bot.channel)) return;

    EventHandler eventHandler = new EventHandler();

    eventHandler.onDiscordChat(event.getMessage());
  }
}
