package com.tropicoss.alfred.bot;

import com.google.gson.JsonObject;
import com.tropicoss.alfred.Alfred;
import com.tropicoss.alfred.PlayerInfoFetcher;
import com.tropicoss.alfred.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static com.tropicoss.alfred.Alfred.LOGGER;

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

    private Webhook WEBHOOK = null;

    private Bot() throws InterruptedException {
        try {
            BOT = JDABuilder.createDefault(Config.Bot.token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new Listeners())
                    .build()
                    .awaitReady();

            CHANNEL = BOT.getTextChannelById(Config.Bot.channel);

            for (Webhook webhook : CHANNEL.getGuild().retrieveWebhooks().complete()) {
                if ("Alfred".equals(webhook.getName())) {
                    WEBHOOK = webhook;
                }
            }

            if(WEBHOOK == null) {
                WEBHOOK = CHANNEL.createWebhook("Alfred").complete();
            }

        } catch (Exception e) {
            switch (e.getClass().getSimpleName()) {
                case "InvalidTokenException":
                    Alfred.LOGGER.error("Invalid bot token. Please check your config file.");
                    break;
                case "IllegalArgumentException":
                    Alfred.LOGGER.error("Invalid bot channel. Please check your config file.");
                    break;
                default:
                    Alfred.LOGGER.error("Error starting bot: " + e.getMessage());
                    break;
            }
            throw e;
        }
    }

    public static Bot getInstance() {
        return instance;
    }

    public void onStartUp() {
        CHANNEL
                .sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(Config.Generic.name)
                                .setDescription("Server has started!")
                                .setFooter(Config.Generic.name)
                                .setTimestamp(Instant.now())
                                .setColor(39129)
                                .build())
                .queue();
    }

    public void shutdown() {
        CHANNEL
                .sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(Config.Generic.name)
                                .setDescription("Server has shut down!")
                                .setFooter(Config.Generic.name)
                                .setTimestamp(Instant.now())
                                .setColor(39129)
                                .build())
                .queue();

        try {
            BOT.shutdownNow();
        } catch (Exception e) {
            return;
        }
    }

    public void sendEmbedMessage(
            String message, @Nullable PlayerInfoFetcher.Profile profile, String ServerName) {

        if (CHANNEL == null) {
            Alfred.LOGGER.error("Chat channel not found. Please check your config file.");
            return;
        }

        EmbedBuilder builder =
                new EmbedBuilder()
                        .setDescription(message)
                        .setFooter(ServerName)
                        .setTimestamp(Instant.now())
                        .setColor(39129);

        if (profile != null) {
            builder.setAuthor(
                    profile.data.player.username,
                    String.format("https://namemc.com/profile/%s", profile.data.player.username),
                    profile.data.player.avatar);
        } else {
            builder.setAuthor(ServerName);
        }

        CHANNEL.sendMessageEmbeds(builder.build()).queue();
    }

    public void sendWebhook(String message, PlayerInfoFetcher.Profile profile, String serverName) {
        try {
            JsonObject body = new JsonObject();

            body.addProperty("username", String.format("%s - %s", profile.data.player.username, serverName));
            body.addProperty("content", message);
            body.addProperty("avatar_url", profile.data.player.avatar);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://discord.com/api/webhooks/1179988546354950144/pYSIyPfaBNuCZ2DCmIVzq5GMA0A2p7tG6UGEyH5LCQrJKdH7Wc01fEEFQM0q29E795ZF"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        }  catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
