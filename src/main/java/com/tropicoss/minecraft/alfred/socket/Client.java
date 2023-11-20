package com.tropicoss.minecraft.alfred.socket;

import com.tropicoss.minecraft.alfred.AbstractMessage;
import com.tropicoss.minecraft.alfred.Alfred;
import com.tropicoss.minecraft.alfred.MessageType;
import com.tropicoss.minecraft.alfred.PlayerInfoFetcher;
import com.tropicoss.minecraft.alfred.common.MessageSerializer;
import com.tropicoss.minecraft.alfred.config.Config;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;

import static com.tropicoss.minecraft.alfred.Alfred.LOGGER;

public class Client extends WebSocketClient {

    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        LOGGER.info("╔═══════════════════════════════════════╗");
        LOGGER.info("║         Connected To Server           ║");
        LOGGER.info("╚═══════════════════════════════════════╝");

        try {
            AbstractMessage msg =
                    new AbstractMessage.ServerMessage(Config.Generic.name, "Connected to server") {
                    };

            String json = MessageSerializer.serialize(msg);

            Alfred.SOCKET_CLIENT.send(json);
        } catch (Exception e) {
            LOGGER.error("Error sending message: " + e.getMessage());
        }
    }

    @Override
    public void connect() {
        try {

            super.connectBlocking();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(String message) {

        AbstractMessage msg;
        try {
            msg = MessageSerializer.deserialize(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MessageType type = msg.getMessageType();

        switch (type) {
            case DISCORD_MESSAGE:
                handleDiscordMessage((AbstractMessage.DiscordMessage) msg);
                break;
            case CLIENT_MESSAGE:
                handleClientMessage((AbstractMessage.ClientMessage) msg);
                break;
            case SERVER_MESSAGE:
                handleServerMessage((AbstractMessage.ServerMessage) msg);
                break;
            default:
                LOGGER.error("Unknown message type: " + type);
                break;
        }
    }

    private void handleDiscordMessage(AbstractMessage.DiscordMessage msg) {
        LOGGER.info(String.format("[%s] %s: %s", msg.getOrigin(), msg.getName(), msg.getMessage()));

        Text text =
                Text.of(
                        String.format("§9[%s] §b%s: §f%s", msg.getOrigin(), msg.getName(), msg.getMessage()));

        Alfred.SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));
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

        Alfred.SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));
    }

    private void handleServerMessage(AbstractMessage.ServerMessage msg) {

        LOGGER.info(String.format("[%s] %s: %s", msg.getOrigin(), msg.getOrigin(), msg.getMessage()));

        Text text =
                Text.of(
                        String.format("§9[%s] §b%s: §f%s", msg.getOrigin(), msg.getOrigin(), msg.getMessage()));

        Alfred.SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));
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
