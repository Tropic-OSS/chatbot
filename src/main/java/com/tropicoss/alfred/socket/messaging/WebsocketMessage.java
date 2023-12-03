package com.tropicoss.alfred.socket.messaging;

import net.minecraft.text.Text;

public interface WebsocketMessage {

    public String toConsoleString();

    public Text toChatText();
}
