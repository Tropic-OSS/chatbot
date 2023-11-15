package com.tropicoss.guardian.socket;

import com.tropicoss.guardian.config.Config;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static com.tropicoss.guardian.Guardian.LOGGER;
import static com.tropicoss.guardian.Guardian.SERVER;

public class Client extends WebSocketClient {
  public Client(URI serverUri) {
    super(serverUri);
  }

  public static void main(String[] args) {
    Client client = new Client(URI.create("ws://localhost:9090"));
    client.connect();

    client.send("Hello");
  }

  @Override
  public void onOpen(ServerHandshake handshake) {}

  @Override
  public void onMessage(String message) {

    LOGGER.info(String.format("[%s] %s: %s", Config.Generic.name, "User", message));

    Text text = Text.of(String.format("§9[%s] §b%s: §f%s", Config.Generic.name, "User", message));

    for (ServerPlayerEntity player : SERVER.getPlayerManager().getPlayerList()) {
      player.sendMessage(text, false);
    }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {}

  @Override
  public void onError(Exception ex) {}

    public void onGameChat(
        MinecraftServer minecraftServer, Text text, ServerPlayerEntity serverPlayerEntity) {
      sendMessage(getEmbedBuilder(text.getString(), serverPlayerEntity, Config.Generic.name).build());
    }
}