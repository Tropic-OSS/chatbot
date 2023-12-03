package com.tropicoss.alfred.bot;

import com.google.gson.Gson;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.config.GenericConfig;
import com.tropicoss.alfred.socket.messages.DiscordMessage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

import static com.tropicoss.alfred.Alfred.MINECRAFT_SERVER;
import static com.tropicoss.alfred.Alfred.SOCKET_SERVER;
import static com.tropicoss.alfred.Alfred.LOGGER;

public class Listeners extends ListenerAdapter {
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

        String json = new Gson().toJson(msg);

        if (Config.Generic.mode.equals(GenericConfig.Mode.SERVER)) {
            SOCKET_SERVER.broadcast(json);
        }
    }
}
