package com.tropicoss.alfred.socket;

import net.minecraft.text.Text;

public class StartingMessage extends WebsocketMessage {

    public String origin;
    private final String type = "starting";

    public StartingMessage(String origin) {
        this.origin = origin;
    }

    @Override
    public String toConsoleString() {
        return String.format("[%s] Server Starting...", this.origin);
    }

    @Override
    public Text toChatText() {
        return Text.of(String.format("§9[%s] §fServer Starting...", this.origin));
    }
}
