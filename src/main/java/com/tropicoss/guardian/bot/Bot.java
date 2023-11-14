package com.tropicoss.guardian.bot;

import static com.tropicoss.guardian.Mod.*;
import static com.tropicoss.guardian.Mod.SERVER;

import com.tropicoss.guardian.bot.adapters.MessagesAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

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

  public void onStartUp() {
    EmbedBuilder embedBuilder = new EmbedBuilder()
            .setAuthor(SERVER.getName())
            .setDescription("Server has started!")
            .setFooter(SERVER.getName())
            .setTimestamp(Instant.now())
            .setColor(35406);
    sendMessage(embedBuilder.build());
  }

  public void onShutDown() {
    EmbedBuilder embedBuilder = new EmbedBuilder()
            .setAuthor(SERVER.getName())
            .setDescription("Server has shut down!")
            .setFooter(SERVER.getName())
            .setTimestamp(Instant.now())
            .setColor(16065893);
    sendMessage(embedBuilder.build());

    BOT.shutdown();
  }

  public void onServerTick() {
    // Update player count every 5 seconds (100 ticks)
    if (SERVER.getTicks() % 100 == 0) {
      if (playerCount != SERVER.getCurrentPlayerCount()) {
        playerCount = SERVER.getCurrentPlayerCount();
        BOT.getPresence()
            .setActivity(
                Activity.customStatus(
                    String.format(
                        "%d/%d players",
                        SERVER.getCurrentPlayerCount(), SERVER.getMaxPlayerCount())));
      }
    }
  }

  public void onGameChat(MinecraftServer minecraftServer, Text text, ServerPlayerEntity serverPlayerEntity) {
      sendMessage(getEmbedBuilder(text.getString(), serverPlayerEntity, SERVER.getName()).build());
  }

  public void onServerMessage(MinecraftServer minecraftServer, Text text) {
    sendMessage(getEmbedBuilder(text.getString(),null, SERVER.getName()).build());
  }

  public void onDiscordChat(Message message) {
    if (message.getAuthor().isBot()) return;

    if (message.getChannel() != CHANNEL) return;

    if(!message.getEmbeds().isEmpty()) return;

    LOGGER.info(String.format("[Discord] %s: %s", message.getAuthor().getName(), message.getContentRaw()));

    Text text = Text.of(String.format("§9[Discord] §b%s: §f%s",  Objects.requireNonNull(message.getGuild().getMember(message.getAuthor())).getEffectiveName(), message.getContentRaw()));

    for (ServerPlayerEntity player : SERVER.getPlayerManager().getPlayerList()) {
      player.sendMessage(text, false);
    }
  }

  public EmbedBuilder getEmbedBuilder(String message, @Nullable ServerPlayerEntity player, String ServerName) {

    if (player == null) {
      return new EmbedBuilder()
              .setAuthor(ServerName)
              .setDescription(message)
              .setFooter(ServerName)
              .setTimestamp(Instant.now())
              .setColor(4321431);
    }

    return new EmbedBuilder()
            .setAuthor(player.getName().getString(), String.format("https://namemc.com/profile/%s", player.getName().getString()), String.format("https://minotar.net/avatar/%s/100.png", player.getUuidAsString()))
            .setDescription(message)
            .setFooter(ServerName)
            .setTimestamp(Instant.now())
            .setColor(39129);
  }

  public void sendMessage(MessageEmbed embed) {

    if (CHANNEL == null) {
      LOGGER.error("Chat channel not found. Please check your config file.");
      return;
    }

    CHANNEL.sendMessageEmbeds(embed).queue();
  }
}
