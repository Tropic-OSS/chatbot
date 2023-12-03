package com.tropicoss.alfred.bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.config.GenericConfig;
import com.tropicoss.alfred.socket.messages.DiscordMessage;
import com.tropicoss.alfred.socket.messages.InterfaceAdapter;
import com.tropicoss.alfred.socket.messages.WebsocketMessage;
import com.tropicoss.alfred.socket.messages.WebsocketMessageTypeAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

import static com.tropicoss.alfred.Alfred.*;

public class Listeners extends ListenerAdapter {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(WebsocketMessage.class, new WebsocketMessageTypeAdapter())
            .registerTypeHierarchyAdapter(WebsocketMessage.class, new InterfaceAdapter<>())
            .create();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (!event.getChannel().getId().equals(Config.Bot.channel)) return;

        String member = Objects.requireNonNull(event.getGuild().getMember(event.getAuthor()))
                .getEffectiveName();

        DiscordMessage msg = new DiscordMessage(event.getMessage().getContentRaw(), member);

        LOGGER.info(msg.toConsoleString());

        // Send message to all players. Broadcast adds the colors to the console sadly.
        MINECRAFT_SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(msg.toChatText(), false));

        String json = gson.toJson(msg);

        if (Config.Generic.mode.equals(GenericConfig.Mode.SERVER)) {
            SOCKET_SERVER.broadcast(json);
        }
    }
}
