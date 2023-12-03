package com.tropicoss.alfred.socket.messages;

import net.minecraft.text.Text;

public record ServerMessage(String message, String origin) implements WebsocketMessage {

    @Override
    public String getMessageType() {
        return "server";
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server: %s", this.origin(), this.message());
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §bServer: §f%s", this.origin(), this.message()));
    }
}
