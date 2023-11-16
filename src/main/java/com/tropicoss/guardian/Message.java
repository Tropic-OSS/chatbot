package com.tropicoss.guardian;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class Message {
  private final String origin;
  private final String sender;
  private final String content;

  public Message(String origin, String sender, String content) {
    this.origin = origin;
    this.sender = sender;
    this.content = content;
  }

  public String getOrigin() {
    return origin;
  }

  public String getSender() {
    return sender;
  }

  public String getContent() {
    return content;
  }

  public static class MessageSerializer implements JsonSerializer<Message> {
    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("origin", src.origin);
      jsonObject.addProperty("sender", src.sender);
      jsonObject.addProperty("content", src.content);
      return jsonObject;
    }
  }

  // Gson deserializer for Message class
  public static class MessageDeserializer implements JsonDeserializer<Message> {
    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonObject jsonObject = json.getAsJsonObject();
      String origin = jsonObject.get("origin").getAsString();
      String sender = jsonObject.get("sender").getAsString();
      String content = jsonObject.get("content").getAsString();

      return new Message(origin, sender, content);
    }
  }
}
