package com.tropicoss.guardian.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tropicoss.guardian.AbstractMessage;
import java.io.IOException;

public class MessageSerializer {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String serialize(AbstractMessage message) throws JsonProcessingException {
    return objectMapper.writeValueAsString(message);
  }

  public static AbstractMessage deserialize(String jsonString) throws IOException {
    return objectMapper.readValue(jsonString, AbstractMessage.class);
  }
}
