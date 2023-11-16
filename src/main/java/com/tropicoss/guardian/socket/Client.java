package com.tropicoss.guardian.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.guardian.Message;
import com.tropicoss.guardian.config.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;

import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;

public class Client extends WebSocketClient {

  private static MinecraftServer SERVER;
  private static final Logger LOGGER = LoggerFactory.getLogger("Guardian WebSocket Client");

  public Client(URI serverUri, MinecraftServer server) {
    super(serverUri);
    SERVER = server;
  }

  @Override
  public void onOpen(ServerHandshake handshake) {
    LOGGER.info("╔═══════════════════════════════════════╗");
    LOGGER.info("║         Connected To Server           ║");
    LOGGER.info("╚═══════════════════════════════════════╝");
  }

  @Override
  public void onMessage(String message) {

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

}