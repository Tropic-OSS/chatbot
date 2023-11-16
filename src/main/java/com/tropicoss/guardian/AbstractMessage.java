package com.tropicoss.guardian;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tropicoss.guardian.serialization.MessageDeserializer;

@JsonDeserialize(using = MessageDeserializer.class)
public abstract class AbstractMessage {
  protected final String origin;

  public AbstractMessage(String origin) {
    this.origin = origin;
  }

  public String getOrigin() {
    return origin;
  }

  public abstract MessageType getMessageType();

  public static class ServerMessage extends AbstractMessage {
    private final String message;
    private final MessageType messageType = MessageType.SERVER_MESSAGE;

    public ServerMessage(String origin, String message) {
      super(origin);
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public MessageType getMessageType() {
      return messageType;
    }
  }

  public static class ClientMessage extends AbstractMessage {
    private final String message;
    private final String playerUUID;
    private final MessageType messageType = MessageType.CLIENT_MESSAGE;

    public ClientMessage(String origin, String playerUUID, String message) {
      super(origin);
      this.playerUUID = playerUUID;
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    public String getPlayerUUID() {
      return playerUUID;
    }

    @Override
    public MessageType getMessageType() {
      return messageType;
    }
  }

  public static class DiscordMessage extends AbstractMessage {
    private final String message;
    private final String name;
    private final MessageType messageType = MessageType.DISCORD_MESSAGE;

    public DiscordMessage(String origin, String name, String message) {
      super(origin);
      this.name = name;
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    public String getName() {
      return name;
    }

    @Override
    public MessageType getMessageType() {
      return messageType;
    }
  }
}
