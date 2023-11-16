package com.tropicoss.guardian.bot;

import static com.tropicoss.guardian.Guardian.*;
import static com.tropicoss.guardian.Guardian.SERVER;

import com.tropicoss.guardian.bot.adapters.MessagesAdapter;
import com.tropicoss.guardian.config.Config;
import java.time.Instant;
import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot {
  private static final Bot instance;

  private static final Logger LOGGER = LoggerFactory.getLogger("Guardian Bot");

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
          JDABuilder.createDefault(Config.Bot.token)
              .setChunkingFilter(ChunkingFilter.ALL)
              .setMemberCachePolicy(MemberCachePolicy.ALL)
              .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
              .addEventListeners(new MessagesAdapter())
              .build()
              .awaitReady();

      CHANNEL = BOT.getTextChannelById(Config.Bot.channel);
    } catch (Exception e) {
      switch (e.getClass().getSimpleName()) {
        case "InvalidTokenException":
          LOGGER.error("Invalid bot token. Please check your config file.");
          break;
        case "IllegalArgumentException":
          LOGGER.error("Invalid bot channel. Please check your config file.");
          break;
        default:
          LOGGER.error("Error starting bot: " + e.getMessage());
          break;
      }
      throw e;
    }
  }

  public static Bot getInstance() {
    return instance;
  }

  public void onStartUp() {
    CHANNEL.sendMessageEmbeds(new EmbedBuilder()
            .setAuthor(Config.Generic.name)
            .setDescription("Server has started!")
            .setFooter(Config.Generic.name)
            .setTimestamp(Instant.now())
            .setColor(35406).build())
            .queue();
  }

  public void onShutDown()   {
    CHANNEL.sendMessageEmbeds(new EmbedBuilder()
            .setAuthor(Config.Generic.name)
            .setDescription("Server has shut down!")
            .setFooter(Config.Generic.name)
            .setTimestamp(Instant.now())
            .setColor(16065893).build())
            .queue();

    BOT.shutdownNow();
  }

  public void sendEmbedMessage(String message, @Nullable ServerPlayerEntity player, String ServerName) {

    if (CHANNEL == null) {
      LOGGER.error("Chat channel not found. Please check your config file.");
      return;
    }

    if (player == null) {
      CHANNEL.sendMessageEmbeds(new EmbedBuilder()
              .setAuthor(ServerName)
              .setDescription(message)
              .setFooter(ServerName)
              .setTimestamp(Instant.now())
              .setColor(4321431).build())
              .queue();
    } else {
      CHANNEL.sendMessageEmbeds(new EmbedBuilder()
              .setAuthor(
                      player.getName().getString(),
                      String.format("https://namemc.com/profile/%s", player.getName().getString()),
                      String.format("https://minotar.net/avatar/%s/100.png", player.getUuidAsString())
              )
              .setDescription(message)
              .setFooter(ServerName)
              .setTimestamp(Instant.now())
              .setColor(39129).build())
              .queue();
    }
  }

  public void sendMessage(String message) {
    if (CHANNEL == null) {
      LOGGER.error("Chat channel not found. Please check your config file.");
      return;
    }

    CHANNEL.sendMessage(message).queue();
  }
}
