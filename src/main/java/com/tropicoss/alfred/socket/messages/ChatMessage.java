package com.tropicoss.alfred.socket.messages;

import com.tropicoss.alfred.PlayerInfoFetcher;
import net.minecraft.text.Text;

public record ChatMessage(String origin, String uuid, String content) implements WebsocketMessage {

    public PlayerInfoFetcher.Profile getProfile() {
        return PlayerInfoFetcher.getProfile(this.uuid);
    }

    @Override
    public String getMessageType() {
        return "chat";
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] %s: %s", this.origin(), this.getProfile().data.player.username,
                this.content());
    }

    @Override
    public Text toChatText() {

        return Text.of(String.format("§9[%s] §b%s: §f%s", this.origin(), this.getProfile().data.player.username,
                this.content()));
    }
}