package com.tropicoss.alfred.callback;

import com.google.gson.Gson;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.event.AdvancementEvent;
import com.tropicoss.alfred.socket.messaging.AdvancementMessage;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.tropicoss.alfred.Alfred.SOCKET_CLIENT;
import static com.tropicoss.alfred.Alfred.SOCKET_SERVER;

public class AdvancementCallback implements AdvancementEvent {
    @Override
    public void onGrantCriterion(ServerPlayerEntity player, Advancement advancement, String criterion) {
        AdvancementDisplay advancementDisplay = advancement.getDisplay();

        if(advancementDisplay == null || !advancementDisplay.shouldAnnounceToChat()) return;

        AdvancementMessage advancementMessage = new AdvancementMessage(advancementDisplay.getTitle().getString(), advancementDisplay.getDescription().getString(), player.getUuidAsString());

        String json = new Gson().toJson(advancementMessage);

        switch (Config.Generic.mode){
            case SERVER -> {
                SOCKET_SERVER.broadcast(json);

                Bot.getInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
            }

            case CLIENT -> SOCKET_CLIENT.send(json);

            case STANDALONE -> Bot.getInstance().sendAchievementMessage(advancementMessage.getProfile(), advancementMessage.origin, advancementMessage.title, advancementMessage.description);
        }
    }
}