package com.tropicoss.alfred.socket.messages;

import net.minecraft.text.Text;

public abstract class WebsocketMessage {
    public String getMessageType() {
        return null;
    }

    public String toConsoleString() {
        return null;
    }

    public Text toChatText() {
        return null;
    }
}