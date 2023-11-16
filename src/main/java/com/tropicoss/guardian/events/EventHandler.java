package com.tropicoss.guardian.events;

import static com.tropicoss.guardian.Guardian.SOCKET_CLIENT;
import static com.tropicoss.guardian.Guardian.SOCKET_SERVER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tropicoss.guardian.AbstractMessage;
import com.tropicoss.guardian.PlayerInfoFetcher;
import com.tropicoss.guardian.bot.Bot;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.config.WebSocketConfig;
import com.tropicoss.guardian.serialization.MessageSerializer;
import com.tropicoss.guardian.socket.Client;
import com.tropicoss.guardian.socket.Server;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;
import net.dv8tion.jda.api.entities.Message;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandler
    implements MinecraftEvents.PlayerChat,
        MinecraftEvents.ServerChat,
        DiscordEvents.DiscordChat,
        ServerLifecycleEvents.ServerStarting,
        ServerLifecycleEvents.ServerStarted,
        ServerLifecycleEvents.ServerStopping {
  private static final Logger LOGGER = LoggerFactory.getLogger("Guardian Events");
  private static MinecraftServer SERVER;

  @Override
  public void onPlayerChat(MinecraftServer server, Text text, ServerPlayerEntity sender) {
    try {
      AbstractMessage msg =
          new AbstractMessage.ClientMessage(
              Config.Generic.name, sender.getUuid().toString(), text.getString()) {};

      String json = MessageSerializer.serialize(msg);

      if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
        Bot bot = Bot.getInstance();

        PlayerInfoFetcher.Profile profile =
            PlayerInfoFetcher.getProfile(sender.getUuid().toString());

        bot.sendEmbedMessage(text.getString(), profile, Config.Generic.name);

        SOCKET_SERVER.broadcast(json);
      }

      if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
        SOCKET_CLIENT.send(json);
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

        SOCKET_SERVER.broadcast(json);
      }

      if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
        SOCKET_CLIENT.send(json);
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
        SOCKET_SERVER.broadcast(json);
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

        SOCKET_CLIENT = new Client(URI.create(uri));

        SOCKET_CLIENT.connect();

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
