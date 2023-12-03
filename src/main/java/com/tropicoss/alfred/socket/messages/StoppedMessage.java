package com.tropicoss.alfred.socket.messages;

import net.minecraft.text.Text;

public record StoppedMessage(String server) implements WebsocketMessage {
    @Override
    public String getMessageType() {
        return "stopped";
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server Stopping", this.server);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §fServer Stopping", this.server));
    }
}
