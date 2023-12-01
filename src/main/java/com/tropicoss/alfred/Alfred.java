package com.tropicoss.alfred;

import com.tropicoss.alfred.minecraft.Commands;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.config.WebSocketConfig;
import com.tropicoss.alfred.events.EventHandlerBuilder;
import com.tropicoss.alfred.socket.Client;
import com.tropicoss.alfred.socket.Server;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Alfred implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Alfred");

    public static MinecraftServer SERVER;

    public static Server SOCKET_SERVER;

    public static Client SOCKET_CLIENT;

    @Override
    public void onInitializeServer() {
        try {

            ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);

            if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.SERVER)) {

                new EventHandlerBuilder()
                        .listenToDiscordChat()
                        .listenToPlayerChat()
                        .listenToServerStarting()
                        .listenToServerStarted()
                        .listenToServerStopping();
            }

            if (Config.WebSocket.enabled && Config.WebSocket.type.equals(WebSocketConfig.Type.CLIENT)) {

                Commands.register();

                new EventHandlerBuilder()
                        .listenToPlayerChat()
                        .listenToServerChat()
                        .listenToServerStarting()
                        .listenToServerStarted()
                        .listenToServerStopping();
            }

            LOGGER.info("Alfred Has Started");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
