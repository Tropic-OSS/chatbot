package com.tropicoss.guardian.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.guardian.bot.Bot;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.config.WebSocketConfig;
import com.tropicoss.guardian.socket.Client;
import com.tropicoss.guardian.socket.Server;
import net.dv8tion.jda.api.entities.Message;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;

import static com.tropicoss.guardian.Guardian.SOCKET_CLIENT;
import static com.tropicoss.guardian.Guardian.SOCKET_SERVER;

public class EventHandler implements MinecraftEvents.PlayerChat, MinecraftEvents.ServerChat, DiscordEvents.DiscordChat, ServerLifecycleEvents.ServerStarting, ServerLifecycleEvents.ServerStarted, ServerLifecycleEvents.ServerStopping {
    private static final Logger LOGGER = LoggerFactory.getLogger("Guardian Events");
    private static MinecraftServer SERVER;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(com.tropicoss.guardian.Message.class, new com.tropicoss.guardian.Message.MessageSerializer())
            .registerTypeAdapter(com.tropicoss.guardian.Message.class, new com.tropicoss.guardian.Message.MessageDeserializer())
            .create();

    @Override
    public void onPlayerChat(MinecraftServer server, Text text, ServerPlayerEntity sender) {
        com.tropicoss.guardian.Message msg = new com.tropicoss.guardian.Message(Config.Generic.name, sender.getName().getString(), text.getString());

        String json = gson.toJson(msg);

    if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            Bot bot = Bot.getInstance();

            bot.sendEmbedMessage(text.getString(), sender, Config.Generic.name);

            SOCKET_SERVER.broadcast(json);
        }

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
            SOCKET_CLIENT.send(json);
        }
    }

    @Override
    public void onServerChat(MinecraftServer server, Text text) {
        com.tropicoss.guardian.Message msg = new com.tropicoss.guardian.Message(Config.Generic.name, "Server Event", text.getString());

        String json = gson.toJson(msg);

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            Bot bot = Bot.getInstance();

            bot.sendEmbedMessage(text.getString(), null, Config.Generic.name);

            SOCKET_SERVER.broadcast(json);
        }

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
            SOCKET_CLIENT.send(json);
        }
    }

    @Override
    public void onDiscordChat( Message message ) {
        if (message.getAuthor().isBot()) return;

        if (!message.getEmbeds().isEmpty()) return;

        LOGGER.info(String.format("[Discord] %s: %s", message.getAuthor().getName(), message.getContentRaw()));

        Text text =
                Text.of(
                        String.format(
                                "§9[Discord] §b%s: §f%s",
                                Objects.requireNonNull(message.getGuild().getMember(message.getAuthor()))
                                        .getEffectiveName(),
                                message.getContentRaw()));

        // Send message to all players. Broadcast adds the colors to the console sadly.
        SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));
    }

    @Override
    public void onServerStarted(MinecraftServer server) {
        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            try {
                Bot.getInstance();

                SOCKET_SERVER = new Server(new InetSocketAddress(Config.WebSocket.port));

                SOCKET_SERVER.start();

                Bot.getInstance().onStartUp();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
            try {
                String uri = String.format("ws://%s:%d", Config.WebSocket.host, Config.WebSocket.port);

                SOCKET_CLIENT = new Client(URI.create(uri), SERVER);

                SOCKET_CLIENT.connect();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onServerStarting(MinecraftServer server) { SERVER = server; }

    @Override
    public void onServerStopping(MinecraftServer server) {

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
            try {
                SOCKET_SERVER.stop(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
            SOCKET_CLIENT.close();
        }
    }
}
