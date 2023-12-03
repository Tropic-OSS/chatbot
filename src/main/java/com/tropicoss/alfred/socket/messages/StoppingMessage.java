package com.tropicoss.alfred.socket.messages;

public record StoppingMessage(String server) implements WebsocketMessage {
    @Override
    public String getMessageType() {
        return "stopping";
    }
}
