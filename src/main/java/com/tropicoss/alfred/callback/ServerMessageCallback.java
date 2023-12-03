package com.tropicoss.alfred.callback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.socket.messages.ChatMessage;
import com.tropicoss.alfred.socket.messages.InterfaceAdapter;
import com.tropicoss.alfred.socket.messages.WebsocketMessage;
import com.tropicoss.alfred.socket.messages.WebsocketMessageTypeAdapter;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.tropicoss.alfred.Alfred.SOCKET_CLIENT;
import static com.tropicoss.alfred.Alfred.SOCKET_SERVER;

public class ServerMessageCallback implements ServerMessageEvents.ChatMessage, ServerMessageEvents.CommandMessage {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(WebsocketMessage.class, new WebsocketMessageTypeAdapter())
            .registerTypeHierarchyAdapter(WebsocketMessage.class, new InterfaceAdapter<>())
            .create();

    @Override
    public void onChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {

        ChatMessage msg = new ChatMessage(Config.Generic.name, sender.getUuid().toString(), message.getContent().getString());

        String json = gson.toJson(msg);

        Bot bot = Bot.getInstance();

        switch (Config.Generic.mode) {
            case SERVER -> {
                bot.sendWebhook(message.getContent().getString(), msg.getProfile(), Config.Generic.name);

                SOCKET_SERVER.broadcast(json);
            }

            case STANDALONE -> bot.sendWebhook(message.getContent().getString(), msg.getProfile(), Config.Generic.name);

            case CLIENT -> SOCKET_CLIENT.send(json);
        }
    }

    @Override
    public void onCommandMessage(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {
        final String typeKey = params.type().chat().translationKey();

        switch (typeKey) {
            case "chat.type.emote" -> handleMeCommand(message, source, params);

            case "chat.type.announcement" -> handleSayCommand(message, source, params);
        }
    }

    private void handleSayCommand(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {

    }

    private void handleMeCommand(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {

    }
}
