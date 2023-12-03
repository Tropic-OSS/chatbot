package com.tropicoss.alfred.callback;

import com.google.gson.Gson;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.config.GenericConfig;
import com.tropicoss.alfred.minecraft.Commands;
import com.tropicoss.alfred.socket.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.net.URI;

import static com.tropicoss.alfred.Alfred.*;

public class ServerLifecycleCallback implements ServerLifecycleEvents.ServerStarting,
        ServerLifecycleEvents.ServerStarted,
        ServerLifecycleEvents.ServerStopping,
        ServerLifecycleEvents.ServerStopped {

    @Override
    public void onServerStarted(MinecraftServer server) {

        try {
            RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

            long uptime = rb.getUptime();

            switch (Config.Generic.mode) {
                case SERVER, STANDALONE -> Bot.getInstance().sendServerStartedMessage(Config.Generic.name, uptime);

                case CLIENT -> {

                    StartedMessage message = new StartedMessage(Config.Generic.name, uptime);

                    String json = new Gson().toJson(message);

                    SOCKET_CLIENT.send(json);

                    LOGGER.info("Running in Client Mode");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onServerStarting(MinecraftServer server) {
        MINECRAFT_SERVER = server;

        try {
            switch (Config.Generic.mode) {
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

                    StartingMessage message = new StartingMessage(Config.Generic.name);

                    String json = new Gson().toJson(message);

                    SOCKET_CLIENT.send(json);

                    LOGGER.info("Running in Client Mode");
                }

                case STANDALONE -> {
                    Bot.getInstance().sendServerStartingMessage(Config.Generic.name);

                    LOGGER.info("Running in Standalone Mode");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onServerStopping(MinecraftServer server) {

        try {
            switch (Config.Generic.mode) {
                case SERVER -> {
                    try {
                        Bot.getInstance().sendServerStoppingMessage(Config.Generic.name);

                        Bot.getInstance().shutdown();

                        SOCKET_SERVER.stop(100);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error closing server: " + e.getMessage());
                    }
                }

                case CLIENT -> {

                   StoppingMessage message = new StoppingMessage(Config.Generic.name);

                    String json = new Gson().toJson(message);

                    SOCKET_CLIENT.send(json);
                }

                case STANDALONE -> Bot.getInstance().sendServerStoppingMessage(Config.Generic.name);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onServerStopped(MinecraftServer server) {
        if (Config.Generic.mode.equals(GenericConfig.Mode.CLIENT)) {
            try {
               StoppedMessage message = new StoppedMessage(Config.Generic.name);

                String json = new Gson().toJson(message);

                SOCKET_CLIENT.send(json);

                SOCKET_CLIENT.closeBlocking();
            } catch (InterruptedException e) {
                LOGGER.error("Error closing client: " + e.getMessage());
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
