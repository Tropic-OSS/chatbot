package com.tropicoss.guardian;

import com.tropicoss.guardian.bot.Bot;
import com.tropicoss.guardian.callbacks.ChatMessageCallback;
import com.tropicoss.guardian.callbacks.DiscordChatCallback;
import com.tropicoss.guardian.callbacks.ServerMessageCallback;
import com.tropicoss.guardian.callbacks.WebSocketMessageCallback;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.config.WebSocketConfig;
import com.tropicoss.guardian.minecraft.Commands;
import com.tropicoss.guardian.socket.Client;
import com.tropicoss.guardian.socket.Server;
import java.net.InetSocketAddress;
import java.net.URI;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Guardian implements DedicatedServerModInitializer {

  public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");
  public static MinecraftServer SERVER;
  public static Server SOCKET_SERVER;
  public static Bot BOT;
  public static int playerCount = -1;

  @Override
  public void onInitializeServer() {
    try {

      Commands.register();

      if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {
          BOT = Bot.getInstance();

          ServerLifecycleEvents.SERVER_STARTED.register(
                  server -> {
                      SERVER = server;
                      BOT.onStartUp();
                  });

          ServerLifecycleEvents.SERVER_STOPPING.register(
                  server -> {
                      BOT.onShutDown();
                  });

          ServerTickEvents.END_WORLD_TICK.register(world -> BOT.onServerTick());

          ChatMessageCallback.EVENT.register(BOT::onGameChat);

          ServerMessageCallback.EVENT.register(BOT::onServerMessage);

          DiscordChatCallback.EVENT.register(BOT::onDiscordChat);

          LOGGER.info("Running in server mode.");
          SOCKET_SERVER = new Server(new InetSocketAddress(Config.WebSocket.port));
          SOCKET_SERVER.start();
          WebSocketMessageCallback.EVENT.register(BOT::onWebSocketMessage);
      }

      if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {
          String uri = String.format("ws://%s:%d", Config.WebSocket.host, Config.WebSocket.port);
          Client client = new Client(URI.create(uri));
          client.connect();
          LOGGER.info("Running in client mode.");
          ServerLifecycleEvents.SERVER_STARTED.register(
                  server -> SERVER = server);

          ChatMessageCallback.EVENT.register(client::onGameChat);

          ServerMessageCallback.EVENT.register(client::onServerMessage);
      }

      LOGGER.info("╔═══════════════════════════════════════╗");
      LOGGER.info("║         Guardian Has Started          ║");
      LOGGER.info("╚═══════════════════════════════════════╝");

    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
  }
}
