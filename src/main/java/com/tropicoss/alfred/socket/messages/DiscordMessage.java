package com.tropicoss.alfred.socket.messages;

import net.minecraft.text.Text;

public class DiscordMessage extends WebsocketMessage {

    private final String message;

    private final String member;

    public DiscordMessage(String message, String member) {
        this.message = message;
        this.member = member;
    }

    public String getMessage() {
        return message;
    }

    public String getMember() {
        return member;
    }

    @Override
    public String getMessageType() {
        return "discord";
    }

    @Override
    public String toConsoleString() {
        return String.format("[Discord] %s: %s", this.getMember(), this.getMessage());
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[Discord] §b%s: §f%s", this.getMember(),
                this.getMessage()));
    }
}