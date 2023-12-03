package com.tropicoss.alfred.socket;

import net.minecraft.text.Text;

public class ServerMessage extends WebsocketMessage {
    public String message;
    public String origin;

    private final String type = "server";

    public ServerMessage(String message, String origin) {
        this.message = message;
        this.origin = origin;
    }
    @Override
    public String toConsoleString() {
        return String.format("[%s] Server: %s", this.origin, this.message);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §bServer: §f%s", this.origin, this.message));
    }
}
