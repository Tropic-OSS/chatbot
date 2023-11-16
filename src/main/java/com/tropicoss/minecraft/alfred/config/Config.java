package com.tropicoss.minecraft.alfred.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Config {
    private static final String CONFIG_FILE_NAME = "alfred.json";
    public static BotConfig Bot;
    public static WebSocketConfig WebSocket;
    public static GenericConfig Generic;

    static {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ConfigData configData = loadConfig();

        Bot = configData.Bot;
        WebSocket = configData.WebSocket;
        Generic = configData.Generic;

        saveConfig(configData, gson);
    }

    private static ConfigData loadConfig() {
        Path configFilePath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);

        if (Files.isRegularFile(configFilePath)) {
            try {
                String json = Files.readString(configFilePath);
                return new Gson().fromJson(json, ConfigData.class);
            } catch (IOException e) {
                throw new RuntimeException("Error loading configuration file", e);
            }
        }

        return new ConfigData();
    }

    private static void saveConfig(ConfigData configData, Gson gson) {
        Path configFilePath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        try {
            String json = gson.toJson(configData);
            Files.writeString(
                    configFilePath, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error saving configuration file", e);
        }
    }

    private static class ConfigData {
        BotConfig Bot = new BotConfig();
        WebSocketConfig WebSocket = new WebSocketConfig();
        GenericConfig Generic = new GenericConfig();
    }
}
