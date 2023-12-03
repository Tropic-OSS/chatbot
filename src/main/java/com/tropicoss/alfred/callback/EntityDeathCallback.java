package com.tropicoss.alfred.callback;

import com.google.gson.Gson;
import com.tropicoss.alfred.bot.Bot;
import com.tropicoss.alfred.config.Config;
import com.tropicoss.alfred.event.EntityDeathEvents;
import com.tropicoss.alfred.event.PlayerDeathEvents;
import com.tropicoss.alfred.socket.messaging.EntityDeathMessage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static com.tropicoss.alfred.Alfred.*;
import static java.lang.Math.floor;
import static java.lang.Math.round;

public final class EntityDeathCallback implements PlayerDeathEvents, EntityDeathEvents
{

    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        String message = source.getDeathMessage(player).getString();

        RegistryKey<World> registry = player.getWorld().getRegistryKey();

        String dimension = registry.getValue().toString();

        // Type Casting to Int to remove decimal points
        String coordinates = String.format("*%s at %s, %s, %s*", dimension.replaceAll(".*:", ""), (int) player.getX(), (int) player.getY(), (int) player.getZ());

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

        RegistryKey<World> registry = entity.getWorld().getRegistryKey();

        String dimension = registry.getValue().toString();

        String coordinates = String.format("*%s at %s, %s, %s*", dimension.replaceAll(".*:", ""), (int) entity.getX(), (int) entity.getY(), (int) entity.getZ());

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
