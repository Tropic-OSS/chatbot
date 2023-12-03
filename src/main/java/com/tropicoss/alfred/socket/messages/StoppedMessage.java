package com.tropicoss.alfred.socket.messages;

public record StoppedMessage() implements WebsocketMessage {
    @Override
    public String getMessageType() {
        return "stopped";
    }
}
