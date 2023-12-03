package com.tropicoss.alfred.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import static com.tropicoss.alfred.Alfred.LOGGER;
import static com.tropicoss.alfred.Alfred.MINECRAFT_SERVER;

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
        try {
            Gson gson =  new Gson();

            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

            if (jsonObject.has("type")) {

                String messageType = jsonObject.get("type").getAsString();

                switch (messageType) {
                    case "server":
                        handleServerMessage(gson.fromJson(message, ServerMessage.class));
                        break;
                    case "chat":
                        handleChatMessage(gson.fromJson(message, ChatMessage.class));
                        break;
                    case "starting":
                        handleStartingMessage(gson.fromJson(message, StartingMessage.class));
                        break;
                    case "started":
                        handleStartedMessage(gson.fromJson(message, StartedMessage.class));
                        break;
                    case "stopping":
                        handleStoppingMessage(gson.fromJson(message, StoppingMessage.class));
                        break;
                    case "stopped":
                        handleStoppedMessage(gson.fromJson(message, StoppedMessage.class));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + messageType);
                }
            } else {
                System.out.println("No messageType in JSON object");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
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

        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        Bot.getInstance().sendWebhook(msg.content, msg.getProfile(), msg.origin);
    }

    private void handleServerMessage(ServerMessage msg) {

        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        Bot.getInstance().sendEmbedMessage(msg.message, msg.origin);
    }

    private void handleStartingMessage(StartingMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        Bot.getInstance().sendServerStartingMessage(msg.origin);
    }

    private void handleStartedMessage(StartedMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        Bot.getInstance().sendServerStartedMessage(msg.origin, msg.uptime);
    }

    private void handleStoppingMessage(StoppingMessage msg) {

        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        Bot.getInstance().sendServerStoppingMessage(msg.server);
    }

    private void handleStoppedMessage(StoppedMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER
                .getPlayerManager()
                .getPlayerList()
                .forEach(player -> player.sendMessage(msg.toChatText(), false));

        Bot.getInstance().sendServerStoppedMessage(msg.server);
    }
}