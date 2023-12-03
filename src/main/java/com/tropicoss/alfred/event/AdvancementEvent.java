package com.tropicoss.alfred.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface AdvancementEvent {

    Event<AdvancementEvent> EVENT =
            EventFactory.createArrayBacked(AdvancementEvent.class, callbacks -> (player, advancement, criterion) -> {
                for (AdvancementEvent callback : callbacks) {
                    callback.onGrantCriterion(player, advancement, criterion);
                }
            });

    void onGrantCriterion(ServerPlayerEntity player, Advancement advancement, String criterion);
}
