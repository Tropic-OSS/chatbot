package com.tropicoss.minecraft.alfred.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tropicoss.minecraft.alfred.AbstractMessage;
import com.tropicoss.minecraft.alfred.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageDeserializer extends StdDeserializer<AbstractMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDeserializer.class);

    // DO NOT DELETE
    public MessageDeserializer() {
        this(null);
    }

    public MessageDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AbstractMessage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        // Extract common properties
        String origin = node.get("origin").asText();
        MessageType messageType = MessageType.valueOf(node.get("messageType").asText());

        // Deserialize based on messageType
        switch (messageType) {
            case SERVER_MESSAGE:
                return new AbstractMessage.ServerMessage(origin, node.get("message").asText());
            case CLIENT_MESSAGE:
                return new AbstractMessage.ClientMessage(
                        origin,
                        node.get("playerUUID").asText(),
                        node.get("message").asText()
                );
            case DISCORD_MESSAGE:
                return new AbstractMessage.DiscordMessage(
                        origin,
                        node.get("name").asText(),
                        node.get("message").asText()
                );
            default:
                LOGGER.error("Unsupported message type: " + messageType);
        }
        return null;
    }
}
