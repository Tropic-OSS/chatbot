package com.tropicoss.alfred.socket.messaging;

import com.tropicoss.alfred.config.Config;
import net.minecraft.text.Text;

public class EntityDeathMessage implements WebsocketMessage{

    private final String type = "death";

    public String message;

    public String coordinates;

    public final String origin = Config.Generic.name;

    public EntityDeathMessage(String message, String coordinates) {
        this.message = message;
        this.coordinates = coordinates;
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] [%s] %s ", this.origin, this.coordinates, this.message);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §b[%s] §f%s", this.origin, this.coordinates, this.message));
    }
}
