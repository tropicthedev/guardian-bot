package com.tropicoss.guardian.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;

public class ConfigurationManager {
    private Reader reader;
    private String filePath;
    private JsonObject config;
    private final Gson gson;

    public ConfigurationManager(String filePath) throws FileNotFoundException {
        this(getReaderFromPath(filePath));
        this.filePath = filePath;
    }

    public ConfigurationManager(final Reader reader) {
        this();
        this.reader = reader;
    }

    private ConfigurationManager() {
        this.gson = new Gson();
        this.config = getDefaultConfig();
    }

    private static Reader getReaderFromPath(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if(file.exists()) {
            return new FileReader(file);
        }

        return null;
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

    public void loadConfig() {
      this.config = gson.fromJson(reader, JsonObject.class);
    }

    private void createDefaultConfigFile() throws IOException {
        FileWriter file = new FileWriter(filePath);

        file.write(gson.toJson(config));
    }

//    public void reloadConfig() {
//        loadConfig();
//    }

    public String getSetting(String section, String key) {
        JsonObject sectionObject = config.getAsJsonObject(section);
        return (sectionObject != null && sectionObject.has(key)) ? sectionObject.get(key).getAsString() : "Setting not found";
    }
}
