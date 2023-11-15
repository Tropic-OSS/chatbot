package com.tropicoss.guardian.bot.adapters;

import com.tropicoss.guardian.callbacks.DiscordChatCallback;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static com.tropicoss.guardian.Guardian.SOCKET_SERVER;

public class MessagesAdapter extends ListenerAdapter {
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    DiscordChatCallback.EVENT.invoker().dispatch(event.getMessage());

    SOCKET_SERVER.broadcast(event.getMessage().getContentRaw());
  }
}
