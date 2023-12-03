package com.tropicoss.alfred.socket.messages;

import net.minecraft.text.Text;

public record DiscordMessage(String message, String member) implements WebsocketMessage {

    @Override
    public String getMessageType() {
        return "discord";
    }

    @Override
    public String toConsoleString() {
        return String.format("[Discord] %s: %s", this.member(), this.message());
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[Discord] §b%s: §f%s", this.member(),
                this.message()));
    }
}