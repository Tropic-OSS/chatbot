package com.tropicoss.alfred.socket.messaging;

import com.tropicoss.alfred.PlayerInfoFetcher;
import com.tropicoss.alfred.config.Config;
import net.minecraft.text.Text;

public class AdvancementMessage implements WebsocketMessage{

    private final String type = "advancement";
    public String title;

    public String description;

    public String uuid;

    public final String origin = Config.Generic.name;

    public AdvancementMessage(String title, String description, String uuid) {
        this.title = title;
        this.description = description;
        this.uuid = uuid;
    }

    public PlayerInfoFetcher.Profile getProfile() {
        return PlayerInfoFetcher.getProfile(this.uuid);
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] [%s] %s got an achievement: %s ", this.origin, this.title, getProfile().data.player.username , this.description);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §b[%s] §f%s %s", this.origin, this.title, getProfile().data.player.username, this.description));

    }
}
