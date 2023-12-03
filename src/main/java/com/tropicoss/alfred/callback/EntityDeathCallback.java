package com.tropicoss.alfred.callback;

import com.google.gson.Gson;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.event.EntityDeathEvents;
import com.tropicoss.alfred.event.PlayerDeathEvents;
import com.tropicoss.alfred.socket.messaging.EntityDeathMessage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.tropicoss.alfred.Alfred.*;

public final class EntityDeathCallback implements PlayerDeathEvents, EntityDeathEvents
{

    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        LOGGER.info("Hello");
        String message = source.getDeathMessage(player).getString();
        String coordinates = player.getBlockPos().toString();

        EntityDeathMessage entityDeathMessage = new EntityDeathMessage(message, coordinates);

        String json = new Gson().toJson(entityDeathMessage);

        switch (Config.Generic.mode) {
            case SERVER -> {
                SOCKET_SERVER.broadcast(json);

                Bot.getInstance().sendDeathMessage(Config.Generic.name, message, coordinates);
            }

            case CLIENT -> SOCKET_CLIENT.send(json);

            case STANDALONE -> Bot.getInstance().sendDeathMessage(Config.Generic.name, message, coordinates);
        }
    }

    @Override
    public void onEntityDeath(LivingEntity entity, DamageSource source) {

        if (!entity.hasCustomName()) return;

        String message = source.getDeathMessage(entity).getString();

        String coordinates = entity.getBlockPos().toString();

        EntityDeathMessage entityDeathMessage = new EntityDeathMessage(message, coordinates);

        String json = new Gson().toJson(entityDeathMessage);

        switch (Config.Generic.mode) {
            case SERVER -> {
                SOCKET_SERVER.broadcast(json);

                Bot.getInstance().sendDeathMessage(Config.Generic.name, message, coordinates);
            }

            case CLIENT -> SOCKET_CLIENT.send(json);

            case STANDALONE -> Bot.getInstance().sendDeathMessage(Config.Generic.name, message, coordinates);
        }
    }
}
