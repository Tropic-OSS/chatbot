package com.tropicoss.alfred.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.alfred.config.Config;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static com.tropicoss.alfred.Alfred.*;

public class Client extends WebSocketClient {

    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {

        try {
            LOGGER.info("Connected To Server");

            ServerMessage msg = new ServerMessage("Connected to server", Config.Generic.name);

            String json = new Gson().toJson(msg);

            SOCKET_CLIENT.send(json);

        } catch (Exception e) {
            LOGGER.error("Error sending message: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(String message) {

        try {
            WebsocketMessage msg = new Gson().fromJson(message, WebsocketMessage.class);

            LOGGER.info(msg.toConsoleString());

            MINECRAFT_SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(msg.toChatText(), false));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
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