package com.tropicoss.guardian.socket;

import static com.tropicoss.guardian.Guardian.SOCKET_CLIENT;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.guardian.Message;
import com.tropicoss.guardian.config.Config;
import java.net.URI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends WebSocketClient {

  private static final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(
              com.tropicoss.guardian.Message.class,
              new com.tropicoss.guardian.Message.MessageSerializer())
          .registerTypeAdapter(
              com.tropicoss.guardian.Message.class,
              new com.tropicoss.guardian.Message.MessageDeserializer())
          .create();
  private static final Logger LOGGER = LoggerFactory.getLogger("Guardian WebSocket Client");
  private static MinecraftServer SERVER;

  public Client(URI serverUri, MinecraftServer server) {
    super(serverUri);
    SERVER = server;
  }

  @Override
  public void onOpen(ServerHandshake handshake) {
    LOGGER.info("╔═══════════════════════════════════════╗");
    LOGGER.info("║         Connected To Server           ║");
    LOGGER.info("╚═══════════════════════════════════════╝");

    com.tropicoss.guardian.Message msg =
        new com.tropicoss.guardian.Message(
            Config.Generic.name, "Server Event", "Server has started!");

    SOCKET_CLIENT.send(gson.toJson(msg));
  }

  @Override
  public void onMessage(String message) {

    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(Message.class, new Message.MessageSerializer())
            .registerTypeAdapter(Message.class, new Message.MessageDeserializer())
            .create();

    Message msg = gson.fromJson(message, Message.class);

    LOGGER.info(String.format("[%s] %s: %s", msg.getOrigin(), msg.getSender(), msg.getContent()));

    Text text =
        Text.of(
            String.format("§9[%s] §b%s: §f%s", msg.getOrigin(), msg.getSender(), msg.getContent()));

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
