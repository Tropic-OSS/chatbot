package com.tropicoss.alfred.socket.messages;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class WebsocketMessageTypeAdapter extends TypeAdapter<WebsocketMessage> {

    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, WebsocketMessage value) throws IOException {
        // Write the type information to the JSON
        out.beginObject();
        out.name("type").value(value.getClass().getSimpleName());

        // Serialize the actual object based on its type
        out.name("data");
        gson.toJson(value, value.getClass(), out);

        out.endObject();
    }

    @Override
    public WebsocketMessage read(JsonReader in) throws IOException {
        // Read the type information from the JSON
        in.beginObject();
        in.nextName(); // "type"
        String type = in.nextString();
        in.nextName(); // "data"

        // Deserialize the actual object based on its type
        WebsocketMessage message = switch (type) {
            case "ChatMessage" -> gson.fromJson(in, ChatMessage.class);
            case "DiscordMessage" -> gson.fromJson(in, DiscordMessage.class);
            case "ServerMessage" -> gson.fromJson(in, ServerMessage.class);
            default -> null;
        };

        in.endObject();
        return message;
    }
}

