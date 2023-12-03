package com.tropicoss.alfred.socket.messages;

import net.minecraft.text.Text;

public record StartedMessage(String origin, Long uptime) implements WebsocketMessage {
    @Override
    public String getMessageType() {
        return "started";
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server started in %sS 🕛", this.origin, this.uptime / 1000);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §fServer started in %sS 🕛", this.origin, this.uptime / 1000));
    }
}
