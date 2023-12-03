package com.tropicoss.alfred.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.socket.messages.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import static com.tropicoss.alfred.Alfred.LOGGER;
import static com.tropicoss.alfred.Alfred.MINECRAFT_SERVER;

public class Server extends WebSocketServer {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(WebsocketMessage.class, new WebsocketMessageTypeAdapter())
            .registerTypeHierarchyAdapter(WebsocketMessage.class, new InterfaceAdapter<>())
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

        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        switch (type) {
            case "server":
                handleServerMessage((ServerMessage) msg);
                break;
            case "chat":
                handleChatMessage((ChatMessage) msg);
                break;
            case "starting":
                handleStartingMessage((StartingMessage) msg);
                break;
            case "started":
                handleStartedMessage((StartedMessage) msg);
                break;
            case "stopping":
                handleStoppingMessage((StoppingMessage) msg);
                break;
            case "stopped":
                handleStoppedMessage((StoppedMessage) msg);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
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

    private void handleChatMessage(ChatMessage msg) {

        Bot.getInstance().sendWebhook(msg.content(), msg.getProfile(), msg.origin());
    }

    private void handleServerMessage(ServerMessage msg) {

        Bot.getInstance().sendEmbedMessage(msg.message(), msg.origin());
    }

    private void handleStartingMessage(StartingMessage msg) {
        Bot.getInstance().sendServerStartingMessage(msg.origin());
    }

    private void handleStartedMessage(StartedMessage msg) {
        Bot.getInstance().sendServerStartedMessage(msg.origin(), msg.uptime());
    }

    private void handleStoppingMessage(StoppingMessage msg) {
        Bot.getInstance().sendServerStoppingMessage(msg.server());
    }

    private void handleStoppedMessage(StoppedMessage msg) {
        Bot.getInstance().sendServerStoppedMessage(msg.server());
    }
}