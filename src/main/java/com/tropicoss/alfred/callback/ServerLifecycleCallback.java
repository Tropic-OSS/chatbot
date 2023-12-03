package com.tropicoss.alfred.callback;

import com.tropicoss.alfred.Alfred;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.minecraft.Commands;
import com.tropicoss.alfred.socket.Client;
import com.tropicoss.alfred.socket.Server;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.net.URI;

import static com.tropicoss.alfred.Alfred.SOCKET_SERVER;
import static com.tropicoss.alfred.Alfred.SOCKET_CLIENT;
import static com.tropicoss.alfred.Alfred.LOGGER;

public class ServerLifecycleCallback implements ServerLifecycleEvents.ServerStarting,
        ServerLifecycleEvents.ServerStarted,
        ServerLifecycleEvents.ServerStopping,
        ServerLifecycleEvents.ServerStopped{

    @Override
    public void onServerStarted(MinecraftServer server) {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

        long uptime = rb.getUptime();

        Bot.getInstance().sendServerStartedMessage(Config.Generic.name, uptime);
    }

    @Override
    public void onServerStarting(MinecraftServer server) {
        switch(Config.Generic.mode) {
            case SERVER -> {
                Bot.getInstance().sendServerStartingMessage(Config.Generic.name);

                SOCKET_SERVER = new Server(new InetSocketAddress(Config.WebSocket.port));

                SOCKET_SERVER.start();

                LOGGER.info("Running in Server Mode");
            }
            case CLIENT -> {
                Commands.register();

                String uri = String.format("ws://%s:%d", Config.WebSocket.host, Config.WebSocket.port);

                SOCKET_CLIENT = new Client(URI.create(uri));

                try {
                    SOCKET_CLIENT.connectBlocking();
                } catch (InterruptedException e) {
                    LOGGER.error("There was an error connecting to Alfred Server is it running ?");
                }

                LOGGER.info("Running in Client Mode");
            }
            case STANDALONE -> {
                Bot.getInstance().sendServerStartingMessage(Config.Generic.name);

                LOGGER.info("Running in Standalone Mode");
            }
        }
    }

    @Override
    public void onServerStopping(MinecraftServer server) {

        switch (Config.Generic.mode) {
            case SERVER -> {
                try {
                    Bot.getInstance().sendServerStoppingMessage(Config.Generic.name);

                    Bot.getInstance().shutdown();
                    SOCKET_SERVER.stop(100);
                } catch (InterruptedException e) {
                    Alfred.LOGGER.error("Error closing server: " + e.getMessage());
                }
            }

            case CLIENT -> {
                try {
                    Alfred.SOCKET_CLIENT.closeBlocking();
                } catch (InterruptedException e) {
                    Alfred.LOGGER.error("Error closing client: " + e.getMessage());
                }
            }

            case STANDALONE -> Bot.getInstance().sendServerStoppingMessage(Config.Generic.name);
        }
    }

    @Override
    public void onServerStopped(MinecraftServer server) {

    }
}
