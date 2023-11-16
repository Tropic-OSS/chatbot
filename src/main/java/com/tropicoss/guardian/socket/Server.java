package com.tropicoss.guardian.socket;

import static com.tropicoss.guardian.Guardian.SERVER;

import com.tropicoss.guardian.AbstractMessage;
import com.tropicoss.guardian.MessageType;
import com.tropicoss.guardian.PlayerInfoFetcher;
import com.tropicoss.guardian.bot.Bot;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.serialization.MessageSerializer;
import java.io.IOException;
import java.net.InetSocketAddress;
import net.minecraft.text.Text;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends WebSocketServer {

  private static final Logger LOGGER = LoggerFactory.getLogger("Guardian WebSocket Server");

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

    SERVER
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

    SERVER
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
