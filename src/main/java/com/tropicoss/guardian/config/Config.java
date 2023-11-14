package com.tropicoss.guardian.config;

import static com.tropicoss.guardian.Mod.LOGGER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class Config {
  public String prefix = "!";
  public String token = "";
  public String chatChannel = "";
  private static Config instance;

  public static Config getInstance() {
    return instance;
  }

  public static void load(File file) {
    if (!file.exists()) {
      createDefaultConfig(file);
    } else {
      instance = fromFile(file);
    }
  }

  private static void createDefaultConfig(File configFile) {
    try {
      instance = new Config();
      saveToFile(configFile);
      LOGGER.info("Default config file created: " + configFile.getAbsolutePath());
    } catch (IOException e) {
      handleConfigError("Error creating default config file: " + e.getMessage());
    }
  }

  private static void saveToFile(File configFile) throws IOException {
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write(instance.toString());
      LOGGER.info("Default config saved to file: " + configFile.getAbsolutePath());
    }
  }

  private static Config fromFile(File configFile) {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(configFile)))) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.fromJson(reader, Config.class);
    } catch (FileNotFoundException e) {
      handleConfigError("Config file not found or empty. Stopping the server...");
    } catch (IOException e) {
      handleConfigError("Error reading config file: " + e.getMessage());
    }
    return null;
  }

  private static void handleConfigError(String errorMessage) {
    LOGGER.error("╔═══════════════════════════════════════╗");
    LOGGER.error("║     ERROR: Config File Issue          ║");
    LOGGER.error("║                                       ║");
    LOGGER.error("║ " + errorMessage);
    LOGGER.error("╚═══════════════════════════════════════╝");
    System.exit(0);
  }

  @Override
  public String toString() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(this);
  }
}
