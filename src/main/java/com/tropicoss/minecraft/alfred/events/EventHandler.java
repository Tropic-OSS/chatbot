package com.tropicoss.minecraft.alfred.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tropicoss.minecraft.alfred.AbstractMessage;
import com.tropicoss.minecraft.alfred.Alfred;
import com.tropicoss.minecraft.alfred.PlayerInfoFetcher;
import com.tropicoss.minecraft.alfred.bot.Bot;
import com.tropicoss.minecraft.alfred.common.MessageSerializer;
import com.tropicoss.minecraft.alfred.config.Config;
import com.tropicoss.minecraft.alfred.config.WebSocketConfig;
import com.tropicoss.minecraft.alfred.socket.Client;
import com.tropicoss.minecraft.alfred.socket.Server;
import net.dv8tion.jda.api.entities.Message;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;

import static com.tropicoss.minecraft.alfred.Alfred.LOGGER;

public class EventHandler
        implements MinecraftEvents.PlayerChat,
        MinecraftEvents.ServerChat,
        DiscordEvents.DiscordChat,
        ServerLifecycleEvents.ServerStarting,
        ServerLifecycleEvents.ServerStarted,
        ServerLifecycleEvents.ServerStopping {
    private static MinecraftServer SERVER;

    @Override
    public void onPlayerChat(MinecraftServer server, Text text, ServerPlayerEntity sender) {
        try {
            AbstractMessage msg =
                    new AbstractMessage.ClientMessage(
                            Config.Generic.name, sender.getUuid().toString(), text.getString()) {
                    };

            String json = MessageSerializer.serialize(msg);

            if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
                Bot bot = Bot.getInstance();

                PlayerInfoFetcher.Profile profile =
                        PlayerInfoFetcher.getProfile(sender.getUuid().toString());

                bot.sendEmbedMessage(text.getString(), profile, Config.Generic.name);

                Alfred.SOCKET_SERVER.broadcast(json);
            }

            if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
                Alfred.SOCKET_CLIENT.send(json);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing message: " + e.getMessage());
        }
    }

    @Override
    public void onServerChat(MinecraftServer server, Text text) {
        try {
            AbstractMessage.ServerMessage msg =
                    new AbstractMessage.ServerMessage(Config.Generic.name, text.getString());

            String json = MessageSerializer.serialize(msg);

            if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
                Bot bot = Bot.getInstance();

                bot.sendEmbedMessage(text.getString(), null, Config.Generic.name);

                Alfred.SOCKET_SERVER.broadcast(json);
            }

            if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
                Alfred.SOCKET_CLIENT.send(json);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing message: " + e.getMessage());
        }
    }

    @Override
    public void onDiscordChat(Message message) {
        try {
            if (message.getAuthor().isBot()) return;

            if (!message.getEmbeds().isEmpty()) return;

            var member =
                    Objects.requireNonNull(message.getGuild().getMember(message.getAuthor()))
                            .getEffectiveName();

            LOGGER.info(String.format("[Discord] %s: %s", member, message.getContentRaw()));

            Text text = Text.of(String.format("§9[Discord] §b%s: §f%s", member, message.getContentRaw()));

            // Send message to all players. Broadcast adds the colors to the console sadly.
            SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));

            AbstractMessage.DiscordMessage msg =
                    new AbstractMessage.DiscordMessage("Discord", member, message.getContentRaw());

            String json = MessageSerializer.serialize(msg);

            if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
                Alfred.SOCKET_SERVER.broadcast(json);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing message: " + e.getMessage());
        }
    }

    @Override
    public void onServerStarted(MinecraftServer server) {
        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            try {
                // Starts the Bot
                Bot.getInstance();

                Alfred.SOCKET_SERVER = new Server(new InetSocketAddress(Config.WebSocket.port));

                Alfred.SOCKET_SERVER.start();

                Bot.getInstance().onStartUp();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
            try {
                String uri = String.format("ws://%s:%d", Config.WebSocket.host, Config.WebSocket.port);

                Alfred.SOCKET_CLIENT = new Client(URI.create(uri));

                Alfred.SOCKET_CLIENT.connect();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onServerStarting(MinecraftServer server) {
        SERVER = server;
    }

    @Override
    public void onServerStopping(MinecraftServer server) {

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            try {
                Bot.getInstance().onShutDown();
                Alfred.SOCKET_SERVER.stop(100);
            } catch (InterruptedException e) {
                LOGGER.error("Error closing server: " + e.getMessage());
            }
        }

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
            try {
                Alfred.SOCKET_CLIENT.closeBlocking();
            } catch (InterruptedException e) {
                LOGGER.error("Error closing client: " + e.getMessage());
            }
        }
    }
}
