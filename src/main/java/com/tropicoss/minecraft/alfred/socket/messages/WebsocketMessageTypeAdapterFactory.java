package com.tropicoss.minecraft.alfred.socket.messages;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class WebsocketMessageTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (WebsocketMessage.class.isAssignableFrom(type.getRawType())) {
            return (TypeAdapter<T>) new WebsocketMessageTypeAdapter();
        }
        return null;
    }
}

