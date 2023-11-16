package com.tropicoss.guardian.socket;

import static com.tropicoss.guardian.Guardian.SERVER;
import static com.tropicoss.guardian.Guardian.SOCKET_CLIENT;

import com.tropicoss.guardian.AbstractMessage;
import com.tropicoss.guardian.MessageType;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.serialization.MessageSerializer;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends WebSocketClient {
  private static final Logger LOGGER = LoggerFactory.getLogger("Guardian WebSocket Client");

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
          new AbstractMessage.ServerMessage(Config.Generic.name, "Connected to server") {};

      String json = MessageSerializer.serialize(msg);

      SOCKET_CLIENT.send(json);
    } catch (Exception e) {
      LOGGER.error("Error sending message: " + e.getMessage());
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

    SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));
  }

  private void handleClientMessage(AbstractMessage.ClientMessage msg) {
    ServerPlayerEntity playerEntity =
        SERVER.getPlayerManager().getPlayer(UUID.fromString(msg.getPlayerUUID()));

    assert playerEntity != null;

    LOGGER.info(
        String.format(
            "[%s] %s: %s", msg.getOrigin(), playerEntity.getName().getString(), msg.getMessage()));

    Text text =
        Text.of(
            String.format(
                "§9[%s] §b%s: §f%s",
                msg.getOrigin(), playerEntity.getName().getString(), msg.getMessage()));

    SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));
  }

  private void handleServerMessage(AbstractMessage.ServerMessage msg) {

    LOGGER.info(String.format("[%s] %s: %s", msg.getOrigin(), msg.getOrigin(), msg.getMessage()));

    Text text =
        Text.of(
            String.format("§9[%s] §b%s: §f%s", msg.getOrigin(), msg.getOrigin(), msg.getMessage()));

    SERVER.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    LOGGER.info("╔═══════════════════════════════════════╗");
    LOGGER.info("║       Disconnected From Server        ║");
    LOGGER.info("╚═══════════════════════════════════════╝");
  }

  @Override
  public void onError(Exception ex) {
    LOGGER.error("Error from " + this.getURI().getHost(), ex);
  }

  public void reload() {
    try {
      SOCKET_CLIENT.close();
    } catch (Exception e) {
      LOGGER.error("Client failed to close. Was it Connected ??");
    }

    try {
      SOCKET_CLIENT.connect();
    } catch (Exception e) {
      LOGGER.error("Client failed to connect. Is the server running ??");
    }
  }
}
