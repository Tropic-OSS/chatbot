package com.tropicoss.alfred.socket.messages;

import com.tropicoss.alfred.PlayerInfoFetcher;
import net.minecraft.text.Text;

public class ChatMessage extends WebsocketMessage {

    private final String origin;

    private final String uuid;

    private final String content;


    public ChatMessage(String origin, String uuid, String content) {
        this.origin = origin;
        this.uuid = uuid;
        this.content = content;
    }

    public String getOrigin() {
        return origin;
    }

    public String getUuid() {
        return uuid;
    }

    public PlayerInfoFetcher.Profile getProfile() {
        return PlayerInfoFetcher.getProfile(this.uuid);
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getMessageType() {
        return "chat";
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] %s: %s", this.getOrigin(), this.getProfile().data.player.username,
                this.getContent());
    }

    @Override
    public Text toChatText() {

        return Text.of(String.format("§9[%s] §b%s: §f%s", this.getOrigin(), this.getProfile().data.player.username,
                this.getContent()));
    }
}