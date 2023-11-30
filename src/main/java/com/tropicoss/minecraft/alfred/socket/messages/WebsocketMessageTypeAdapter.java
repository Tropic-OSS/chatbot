package com.tropicoss.minecraft.alfred.socket.messages;

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
        WebsocketMessage message = null;
        switch (type) {
            case "ChatMessage":
                message = gson.fromJson(in, ChatMessage.class);
                break;
            case "DiscordMessage":
                message = gson.fromJson(in, DiscordMessage.class);
                break;
            case "ServerMessage":
                message = gson.fromJson(in, ServerMessage.class);
                break;
            // Add more cases for other implementations if needed
        }

        in.endObject();
        return message;
    }
}

