package com.tropicoss.alfred.socket.messages;

import net.minecraft.text.Text;

public record StartingMessage(String origin) implements WebsocketMessage {
    @Override
    public String getMessageType() {
        return "starting";
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server Starting...", this.origin());
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §fServer Starting...", this.origin));
    }
}
