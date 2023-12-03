package com.tropicoss.alfred.socket.messages;

import net.minecraft.text.Text;

public interface WebsocketMessage {
     default String getMessageType() {
        return null;
    }

     default String toConsoleString() {
        return null;
    }

     default Text toChatText() {
        return null;
    }
}