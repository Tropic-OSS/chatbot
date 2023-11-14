package com.tropicoss.guardian.bot;

import static com.tropicoss.guardian.Mod.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Bot {
  private static final Bot instance;

  static {
    try {
      instance = new Bot();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private final JDA BOT;
  private final TextChannel CHANNEL;

  private Bot() throws InterruptedException {
    try {
      BOT =
          JDABuilder.createDefault(CONFIG.token)
              .setChunkingFilter(ChunkingFilter.ALL)
              .setMemberCachePolicy(MemberCachePolicy.ALL)
              .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
              .addEventListeners(new MessagesAdapter())
              .build()
              .awaitReady();

      CHANNEL = BOT.getTextChannelById(CONFIG.chatChannel);
    } catch (Exception e) {
      LOGGER.error("Error starting bot: " + e.getMessage());
      throw e;
    }
  }

  public static Bot getInstance() {
    return instance;
  }

  public void stop() {
    BOT.shutdown();
  }

  public void sendMessage(String message) {

    if (CHANNEL == null) {
      LOGGER.error("Chat channel not found. Please check your config file.");
      return;
    }

    CHANNEL.sendMessage(message).queue();
  }

  public void onGameChat(
      MinecraftServer minecraftServer, Text text, ServerPlayerEntity serverPlayerEntity) {
    sendMessage("<" + serverPlayerEntity.getName().getString() + "> " + text.getString());
  }

  public void onServerMessage(MinecraftServer minecraftServer, Text text) {
    sendMessage(text.getString());
  }

  public void onDiscordChat(Message message) {
    if (message.getAuthor().isBot()) return;

    if (message.getChannel() != CHANNEL) return;

    LOGGER.info(message.getContentRaw());

    for (ServerPlayerEntity player : SERVER.getPlayerManager().getPlayerList()) {
      player.sendMessage(
          Text.of(message.getAuthor().getName() + ": " + message.getContentRaw()), false);
    }
  }
}
