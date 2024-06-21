package com.tropicoss.guardian.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationManager {
    private final String filePath;
    private JsonObject config;
    private final Gson gson;

    public ConfigurationManager(String filePath) {
        this.filePath = filePath;
        this.gson = new Gson();
        this.config = getDefaultConfig();
        loadConfig();
    }

    private JsonObject getDefaultConfig() {
        JsonObject defaultConfig = new JsonObject();

        JsonObject botConfig = new JsonObject();
        botConfig.addProperty("token", "default-bot-token");
        botConfig.addProperty("guildId", "default-guild-id");
        botConfig.addProperty("chatChannel", "chat-channel-id");

        JsonObject serverConfig = new JsonObject();
        serverConfig.addProperty("port", "4467");
        serverConfig.addProperty("host", "localhost");

        JsonObject genericConfig = new JsonObject();
        genericConfig.addProperty("mode", "standalone");
        genericConfig.addProperty("serverName", "your-server-name");

        defaultConfig.add("bot", botConfig);
        defaultConfig.add("server", serverConfig);
        defaultConfig.add("generic", genericConfig);

        return defaultConfig;
    }

    private void loadConfig() {
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            createDefaultConfigFile();
        } else {
            try (FileReader reader = new FileReader(filePath)) {
                this.config = gson.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createDefaultConfigFile() {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(gson.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public String getSetting(String section, String key) {
        JsonObject sectionObject = config.getAsJsonObject(section);
        return (sectionObject != null && sectionObject.has(key)) ? sectionObject.get(key).getAsString() : "Setting not found";
    }
}
