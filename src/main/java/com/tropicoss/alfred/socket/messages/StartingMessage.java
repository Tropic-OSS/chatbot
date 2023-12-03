package com.tropicoss.alfred.socket.messages;

public record StartingMessage(String origin) implements WebsocketMessage {
    @Override
    public String getMessageType() {
        return "starting";
    }
}
