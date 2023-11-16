package com.tropicoss.guardian.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class EventHandlerBuilder {
    private final EventHandler eventHandler;

    public EventHandlerBuilder() {
        this.eventHandler = new EventHandler();
    }

    public EventHandlerBuilder listenToPlayerChat() {
        MinecraftEvents.PLAYER_CHAT_EVENT.register(eventHandler);
        return this;
    }

    public EventHandlerBuilder listenToDiscordChat() {
        DiscordEvents.DISCORD_CHAT_EVENT.register(eventHandler);
        return this;
    }

    public EventHandlerBuilder listenToServerChat() {
        MinecraftEvents.SERVER_CHAT_EVENT.register(eventHandler);
        return this;
    }

    public EventHandlerBuilder listenToServerStarting() {
        ServerLifecycleEvents.SERVER_STARTING.register(eventHandler);
        return this;
    }

    public EventHandlerBuilder listenToServerStarted() {
        ServerLifecycleEvents.SERVER_STARTED.register(eventHandler);
        return this;
    }

    public EventHandlerBuilder listenToServerStopping() {
        ServerLifecycleEvents.SERVER_STOPPING.register(eventHandler);
        return this;
    }

    public EventHandler build() {
        return eventHandler;
    }
}
