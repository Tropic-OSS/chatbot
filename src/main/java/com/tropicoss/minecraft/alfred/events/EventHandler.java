package com.tropicoss.minecraft.alfred.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.minecraft.alfred.Alfred;
import com.tropicoss.minecraft.alfred.bot.Bot;
import com.tropicoss.minecraft.alfred.config.Config;
import com.tropicoss.minecraft.alfred.config.WebSocketConfig;
import com.tropicoss.minecraft.alfred.socket.Client;
import com.tropicoss.minecraft.alfred.socket.Server;
import com.tropicoss.minecraft.alfred.socket.messages.ChatMessage;
import com.tropicoss.minecraft.alfred.socket.messages.DiscordMessage;
import com.tropicoss.minecraft.alfred.socket.messages.ServerMessage;
import com.tropicoss.minecraft.alfred.socket.messages.WebsocketMessageTypeAdapterFactory;
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

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new WebsocketMessageTypeAdapterFactory())
            .create();

    @Override
    public void onPlayerChat(MinecraftServer server, Text text, ServerPlayerEntity sender) {

        ChatMessage msg = new ChatMessage(Config.Generic.name, sender.getUuid().toString(), text.getString());

        String json = gson.toJson(msg);

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            Bot bot = Bot.getInstance();

            bot.sendEmbedMessage(text.getString(), msg.getProfile(), Config.Generic.name);

            Alfred.SOCKET_SERVER.broadcast(json);
        }

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
            Alfred.SOCKET_CLIENT.send(json);
        }
    }

    @Override
    public void onServerChat(MinecraftServer server, Text text) {
        ServerMessage msg = new ServerMessage(text.getString(), Config.Generic.name);

        String json = gson.toJson(msg);

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            Bot bot = Bot.getInstance();

            bot.sendEmbedMessage(text.getString(), null, Config.Generic.name);

            Alfred.SOCKET_SERVER.broadcast(json);
        }

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
            Alfred.SOCKET_CLIENT.send(json);
        }
    }

    @Override
    public void onDiscordChat(Message message) {
        if (message.getAuthor().isBot()) return;

        if (!message.getEmbeds().isEmpty()) return;

        String member = Objects.requireNonNull(message.getGuild().getMember(message.getAuthor()))
                .getEffectiveName();

        DiscordMessage msg = new DiscordMessage(message.getContentRaw(), member);

        LOGGER.info(msg.toConsoleString());

        // Send message to all players. Broadcast adds the colors to the console sadly.
        SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(msg.toChatText(), false));

        String json = gson.toJson(msg);

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            Alfred.SOCKET_SERVER.broadcast(json);
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

                Alfred.SOCKET_CLIENT.connectBlocking();

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