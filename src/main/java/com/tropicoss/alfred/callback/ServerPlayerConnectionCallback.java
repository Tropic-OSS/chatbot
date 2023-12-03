package com.tropicoss.alfred.callback;

import com.google.gson.Gson;
import com.tropicoss.alfred.PlayerInfoFetcher;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.socket.messaging.LoginMessage;
import com.tropicoss.alfred.socket.messaging.LogoutMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import static com.tropicoss.alfred.Alfred.SOCKET_CLIENT;
import static com.tropicoss.alfred.Alfred.SOCKET_SERVER;

public class ServerPlayerConnectionCallback implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {

    private final Gson gson = new Gson();

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        LogoutMessage logoutMessage = new LogoutMessage(Config.Generic.name, handler.player.getUuidAsString());

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(handler.player.getUuidAsString());

        String json = gson.toJson(logoutMessage);

        switch(Config.Generic.mode) {
            case SERVER -> {
                if (profile != null) {
                    Bot.getInstance().sendLeaveMessage(profile, Config.Generic.name);
                }

                SOCKET_SERVER.broadcast(json);
            }

            case CLIENT -> SOCKET_CLIENT.send(json);

            case STANDALONE -> {
                if (profile != null) {
                    Bot.getInstance().sendLeaveMessage(profile, Config.Generic.name);
                }
            }
        }
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        LoginMessage loginMessage = new LoginMessage(Config.Generic.name, handler.player.getUuidAsString());

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(handler.player.getUuidAsString());

        String json = gson.toJson(loginMessage);

        switch(Config.Generic.mode) {
            case SERVER -> {
                if (profile != null) {
                    Bot.getInstance().sendJoinMessage(profile, Config.Generic.name);
                }

                SOCKET_SERVER.broadcast(json);
            }

            case CLIENT -> SOCKET_CLIENT.send(json);

            case STANDALONE -> {
                if (profile != null) {
                    Bot.getInstance().sendJoinMessage(profile, Config.Generic.name);
                }
            }
        }
    }
}
