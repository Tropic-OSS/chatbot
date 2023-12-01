package com.tropicoss.alfred.socket.messages;

import com.tropicoss.alfred.PlayerInfoFetcher;
import net.minecraft.text.Text;

public class ChatMessage extends WebsocketMessage {

    private final String origin;

    private final String uuid;

    private final String content;

    private final PlayerInfoFetcher.Profile profile;

    public ChatMessage(String origin, String uuid, String content) {
        this.origin = origin;
        this.uuid = uuid;
        this.content = content;
        this.profile = PlayerInfoFetcher.getProfile(this.uuid);
    }

    public String getOrigin() {
        return origin;
    }

    public String getUuid() {
        return uuid;
    }

    public PlayerInfoFetcher.Profile getProfile() {
        return profile;
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
        return String.format("[%s] %s: %s", this.getOrigin(), profile.data.player.username,
                this.getContent());
    }

    @Override
    public Text toChatText() {

        return Text.of(String.format("§9[%s] §b%s: §f%s", this.getOrigin(), profile.data.player.username,
                this.getContent()));
    }
}