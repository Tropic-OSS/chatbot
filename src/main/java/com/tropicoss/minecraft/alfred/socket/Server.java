package com.tropicoss.minecraft.alfred.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.minecraft.alfred.Alfred;
import com.tropicoss.minecraft.alfred.bot.Bot;
import com.tropicoss.minecraft.alfred.config.Config;
import com.tropicoss.minecraft.alfred.socket.messages.ChatMessage;
import com.tropicoss.minecraft.alfred.socket.messages.ServerMessage;
import com.tropicoss.minecraft.alfred.socket.messages.WebsocketMessage;
import com.tropicoss.minecraft.alfred.socket.messages.WebsocketMessageTypeAdapterFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import static com.tropicoss.minecraft.alfred.Alfred.LOGGER;

public class Server extends WebSocketServer {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new WebsocketMessageTypeAdapterFactory())
            .create();

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

        WebsocketMessage msg = gson.fromJson(message, WebsocketMessage.class);

        String type = msg.getMessageType();

        switch (type) {
            case "server":
                handleServerMessage((ServerMessage) msg);
                break;
            case "chat":
                handleChatMessage((ChatMessage) msg);
                break;
            default:
                LOGGER.error("Invalid message");
                break;
        }
    }

    private void handleChatMessage(ChatMessage msg) {

        LOGGER.info(msg.toConsoleString());

        Alfred.SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(
                        player -> player.sendMessage(msg.toChatText(), false));

        Bot bot = Bot.getInstance();

        bot.sendEmbedMessage(msg.getContent(), msg.getProfile(), msg.getOrigin());
    }

    private void handleServerMessage(ServerMessage msg) {
        LOGGER.info(msg.toConsoleString());

        Alfred.SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(
                        player -> player.sendMessage(msg.toChatText(), false));

        Bot bot = Bot.getInstance();

        bot.sendEmbedMessage(msg.getMessage(), null, msg.getOrigin());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.error("Error from " + conn.getRemoteSocketAddress().getAddress().getHostAddress(), ex);
    }

    @Override
    public void onStart() {
        LOGGER.info("Socket Server Started");
        LOGGER.info("Listening on port " + Config.WebSocket.port);
    }
}