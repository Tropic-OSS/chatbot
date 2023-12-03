package com.tropicoss.alfred.socket.messages;

public record StartedMessage(String origin, Long uptime) implements WebsocketMessage {
    @Override
    public String getMessageType() {
        return "started";
    }
}
