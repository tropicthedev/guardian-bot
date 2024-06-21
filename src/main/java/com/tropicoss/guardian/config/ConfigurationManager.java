package com.tropicoss.guardian.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationManager {
    private String filePath;
    private JsonObject config;
    private Gson gson;

    public ConfigurationManager(String filePath) {
        this.filePath = filePath;
        this.gson = new Gson();
        this.config = getDefaultConfig();
        loadConfig();
    }

    private JsonObject getDefaultConfig() {
        JsonObject defaultConfig = new JsonObject();
        defaultConfig.addProperty("botToken", "default_value1");
        defaultConfig.addProperty("guildId", "default_value2");
        defaultConfig.addProperty("setting3", "default_value3");
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

    public String getSetting(String key) {
        return config.has(key) ? config.get(key).getAsString() : "Setting not found";
    }
}
