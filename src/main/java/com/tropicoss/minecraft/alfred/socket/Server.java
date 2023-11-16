package com.tropicoss.minecraft.alfred.socket;

import com.tropicoss.minecraft.alfred.AbstractMessage;
import com.tropicoss.minecraft.alfred.Alfred;
import com.tropicoss.minecraft.alfred.MessageType;
import com.tropicoss.minecraft.alfred.PlayerInfoFetcher;
import com.tropicoss.minecraft.alfred.bot.Bot;
import com.tropicoss.minecraft.alfred.common.MessageSerializer;
import com.tropicoss.minecraft.alfred.config.Config;
import net.minecraft.text.Text;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.tropicoss.minecraft.alfred.Alfred.LOGGER;

public class Server extends WebSocketServer {

    public Server(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info(
                "New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LOGGER.info(
                "Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        AbstractMessage msg;
        try {
            msg = MessageSerializer.deserialize(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MessageType type = msg.getMessageType();

        switch (type) {
            case SERVER_MESSAGE:
                handleServerMessage((AbstractMessage.ServerMessage) msg);
                break;
            case CLIENT_MESSAGE:
                handleClientMessage((AbstractMessage.ClientMessage) msg);
                break;
            default:
                break;
        }
    }

    private void handleClientMessage(AbstractMessage.ClientMessage msg) {

        PlayerInfoFetcher.Profile profile = PlayerInfoFetcher.getProfile(msg.getPlayerUUID());

        if (profile == null) {
            LOGGER.error("Error fetching player info for UUID: " + msg.getPlayerUUID());
            return;
        }

        LOGGER.info(
                String.format(
                        "[%s] %s: %s", msg.getOrigin(), profile.data.player.username, msg.getMessage()));

        Text text =
                Text.of(
                        String.format(
                                "§9[%s] §b%s: §f%s",
                                msg.getOrigin(), profile.data.player.username, msg.getMessage()));

        Alfred.SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(
                        player -> player.sendMessage(text, false));

        Bot bot = Bot.getInstance();

        bot.sendEmbedMessage(msg.getMessage(), profile, msg.getOrigin());
    }

    private void handleServerMessage(AbstractMessage.ServerMessage msg) {
        LOGGER.info(String.format("[%s] %s", msg.getOrigin(), msg.getMessage()));

        Text text = Text.of(String.format("§9[%s] §f%s", msg.getOrigin(), msg.getMessage()));

        Alfred.SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(
                        player -> player.sendMessage(text, false));

        Bot bot = Bot.getInstance();

        bot.sendEmbedMessage(msg.getMessage(), null, msg.getOrigin());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.error("Error from " + conn.getRemoteSocketAddress().getAddress().getHostAddress(), ex);
    }

    @Override
    public void onStart() {
        LOGGER.info("╔═══════════════════════════════════════╗");
        LOGGER.info("║         Socket Server Started         ║");
        LOGGER.info("║         Listening on port " + Config.WebSocket.port + "        ║");
        LOGGER.info("╚═══════════════════════════════════════╝");
    }
}
