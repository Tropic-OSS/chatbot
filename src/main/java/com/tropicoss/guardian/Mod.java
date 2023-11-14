package com.tropicoss.guardian;

import com.tropicoss.guardian.bot.Bot;
import com.tropicoss.guardian.callbacks.ChatMessageCallback;
import com.tropicoss.guardian.callbacks.DiscordChatCallback;
import com.tropicoss.guardian.callbacks.ServerMessageCallback;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.minecraft.Commands;
import java.io.File;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mod implements DedicatedServerModInitializer {
  public static final File CONFIG_FILE =
      new File(FabricLoader.getInstance().getConfigDir().toFile(), "guardian.json");
  public static final Logger LOGGER = LoggerFactory.getLogger("Guardian");
  public static Config CONFIG;
  public static MinecraftServer SERVER;
  public static Bot BOT;
  public static int playerCount = -1;

  @Override
  public void onInitializeServer() {
    try {
      Config.load(CONFIG_FILE);

      CONFIG = Config.getInstance();

      Commands.register();

      BOT = Bot.getInstance();

      ServerLifecycleEvents.SERVER_STARTED.register(
          server -> {
            SERVER = server;
            BOT.onStartUp();
          });

      ServerLifecycleEvents.SERVER_STOPPING.register(
          server -> {
            BOT.onShutDown();
          });

      ServerTickEvents.END_WORLD_TICK.register(world -> BOT.onServerTick());

      ChatMessageCallback.EVENT.register(BOT::onGameChat);

      ServerMessageCallback.EVENT.register(BOT::onServerMessage);

      DiscordChatCallback.EVENT.register(BOT::onDiscordChat);

      LOGGER.info("╔═══════════════════════════════════════╗");
      LOGGER.info("║         Guardian Has Started          ║");
      LOGGER.info("╚═══════════════════════════════════════╝");

    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
  }
}
