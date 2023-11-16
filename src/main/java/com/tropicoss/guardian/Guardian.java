package com.tropicoss.guardian;

import com.tropicoss.guardian.events.EventHandlerBuilder;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.config.WebSocketConfig;
import com.tropicoss.guardian.minecraft.Commands;
import com.tropicoss.guardian.socket.Client;
import com.tropicoss.guardian.socket.Server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Guardian implements DedicatedServerModInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger("Guardian");

  public static MinecraftServer SERVER;

  public static Server SOCKET_SERVER;

  public static Client SOCKET_CLIENT;

  @Override
  public void onInitializeServer() {
    try {

      ServerLifecycleEvents.SERVER_STARTED.register(
          server -> SERVER = server);

      Commands.register();

      if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {

          new EventHandlerBuilder()
                  .listenToDiscordChat()
                  .listenToPlayerChat()
                  .listenToServerStarting()
                  .listenToServerStarted()
                  .listenToServerStopping()
                  .build();
      }

      if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {

             new EventHandlerBuilder()
                    .listenToPlayerChat()
                    .listenToServerChat()
                    .listenToServerStarted()
                    .listenToServerStopping()
                    .build();
      }

      LOGGER.info("╔═══════════════════════════════════════╗");
      LOGGER.info("║         Guardian Has Started          ║");
      LOGGER.info("╚═══════════════════════════════════════╝");

    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
  }
}
