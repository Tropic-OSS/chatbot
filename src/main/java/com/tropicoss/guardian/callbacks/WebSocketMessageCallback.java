package com.tropicoss.guardian.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface WebSocketMessageCallback {
  Event<WebSocketMessageCallback> EVENT =
      EventFactory.createArrayBacked(
          WebSocketMessageCallback.class,
          (listeners) ->
              (message) -> {
                for (WebSocketMessageCallback listener : listeners) {
                  listener.dispatch(message);
                }
              });

  void dispatch(String message);
}
