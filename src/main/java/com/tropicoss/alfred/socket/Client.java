package com.tropicoss.alfred.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.alfred.Alfred;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.socket.messages.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static com.tropicoss.alfred.Alfred.LOGGER;
import static com.tropicoss.alfred.Alfred.MINECRAFT_SERVER;

public class Client extends WebSocketClient {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new WebsocketMessageTypeAdapterFactory())
            .create();

    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {

        try {
            LOGGER.info("Connected To Server");

            ServerMessage msg = new ServerMessage("Connected to server", Config.Generic.name);

            String json = gson.toJson(msg, ServerMessage.class);

            Alfred.SOCKET_CLIENT.send(json);

        } catch (Exception e) {
            Alfred.LOGGER.error("Error sending message: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(String message) {

        WebsocketMessage msg = gson.fromJson(message, WebsocketMessage.class);

        String type = msg.getMessageType();

        switch (type) {
            case "discord":
                handleDiscordMessage((DiscordMessage) msg);
                break;
            case "chat":
                handleChatMessage((ChatMessage) msg);
                break;
            case "server":
                handleServerMessage((ServerMessage) msg);
                break;
            default:
                Alfred.LOGGER.error("Unknown message type: " + type);
                break;
        }
    }

    private void handleDiscordMessage(DiscordMessage msg) {
        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(msg.toChatText(), false));
    }

    private void handleChatMessage(ChatMessage msg) {

        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(msg.toChatText(), false));
    }

    private void handleServerMessage(ServerMessage msg) {

        LOGGER.info(msg.toConsoleString());

        MINECRAFT_SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(msg.toChatText(), false));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.info("Disconnected From Server");
    }

    @Override
    public void onError(Exception ex) {
        LOGGER.error("Error from " + this.getURI().getHost(), ex);
    }

    public void reload() {
        try {
            closeBlocking();
            reconnect();
        } catch (InterruptedException e) {
            LOGGER.error("Error reloading connection: " + e.getMessage());
        }
    }
}