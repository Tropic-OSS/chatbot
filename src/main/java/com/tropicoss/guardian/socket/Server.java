package com.tropicoss.guardian.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.guardian.Message;
import com.tropicoss.guardian.bot.Bot;
import com.tropicoss.guardian.config.Config;
import net.minecraft.text.Text;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import static com.tropicoss.guardian.Guardian.SERVER;

public class Server extends WebSocketServer {

  private static final Logger LOGGER = LoggerFactory.getLogger("Guardian WebSocket Server");

  public Server(InetSocketAddress address) {
    super(address);
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    LOGGER.info("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    LOGGER.info("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onMessage(WebSocket conn, String message) {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Message.class, new Message.MessageSerializer())
            .registerTypeAdapter(Message.class, new Message.MessageDeserializer())
            .create();

    Message msg = gson.fromJson(message, Message.class);

    LOGGER.info(String.format("[%s] %s: %s", msg.getOrigin(), msg.getSender(), msg.getContent()));

    Text text =
            Text.of(
                    String.format(
                            "§9[%s] §b%s: §f%s", msg.getOrigin(), msg.getSender(), msg.getContent()));


    SERVER.getPlayerManager().getPlayerList().forEach(player -> {player.sendMessage(text, false);});

    Bot bot = Bot.getInstance();

    var player = SERVER.getPlayerManager().getPlayer(msg.getSender());

    if (player != null) {
      bot.sendEmbedMessage(msg.getContent(), player, msg.getOrigin());
      return;
    }

    bot.sendEmbedMessage(msg.getContent(), null, msg.getOrigin());
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
