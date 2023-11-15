package com.tropicoss.guardian.socket;

import com.tropicoss.guardian.callbacks.WebSocketMessageCallback;
import com.tropicoss.guardian.config.Config;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class Server extends WebSocketServer {

  public Server(InetSocketAddress address) {
    super(address);
  }

  public static void main(String[] args) {
    Server server = new Server(new InetSocketAddress(9090));
    server.start();
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    System.out.println(
        "New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    System.out.println(
        "Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    WebSocketMessageCallback.EVENT.invoker().dispatch(message);
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    System.err.println(
        "Error on connection to "
            + conn.getRemoteSocketAddress().getAddress().getHostAddress()
            + ": "
            + ex.getMessage());
  }

  @Override
  public void onStart() {
    LOGGER.info("╔═══════════════════════════════════════╗");
    LOGGER.info("║         Socket Server Started         ║");
    LOGGER.info("║         Listening on port " + Config.WebSocket.port + "        ║");
    LOGGER.info("╚═══════════════════════════════════════╝");
  }
}
