package com.tropicoss.minecraft.alfred.socket.messages;

import net.minecraft.text.Text;

public class ServerMessage extends WebsocketMessage {

    private final String origin;

    private final String message;

    public ServerMessage(String message, String origin) {
        this.origin = origin;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public String getMessageType() {
        return "server";
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server: %s", this.getOrigin(), this.getMessage());
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §bServer: §f%s", this.getOrigin(), this.getMessage()));
    }
}
