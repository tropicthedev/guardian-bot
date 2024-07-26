package com.tropicoss.guardian.config;

import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurations;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class Config {
    private static Config instance;
    private static final String DEFAULT_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.yml").toString();

    // Create the Server configuration
    @Configuration
    public static class ServerConfiguration {
        private String host = "127.0.0.1";
        private int port = 1234;

        // Getters and Setters
        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    @Configuration
    public static class WelcomeConfiguration{
        private String channel = "channel-id";
        private String message = "message-id";

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // Create the Bot configuration
    @Configuration
    public static class BotConfiguration {
        private String channel = "channel-id";
        private String token = "bot-token";
        private String guild = "guild-id";

        // Getters and Setters
        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getGuild() {
            return guild;
        }
    }

    // Create the Generic configuration
    @Configuration
    public static class GenericConfiguration {
        private String mode = "default-mode";
        private String name = "default-name";

        // Getters and Setters
        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Main configuration class that combines the above configurations
    @Configuration
    public static class MainConfiguration {
        private ServerConfiguration server = new ServerConfiguration();
        private BotConfiguration bot = new BotConfiguration();
        private GenericConfiguration generic = new GenericConfiguration();
        private WelcomeConfiguration welcome = new WelcomeConfiguration();

        // Getters and Setters for each configuration
        public ServerConfiguration getServer() {
            return server;
        }

        public void setServer(ServerConfiguration server) {
            this.server = server;
        }

        public BotConfiguration getBot() {
            return bot;
        }

        public void setBot(BotConfiguration bot) {
            this.bot = bot;
        }

        public GenericConfiguration getGeneric() {
            return generic;
        }

        public void setGeneric(GenericConfiguration generic) {
            this.generic = generic;
        }

        public WelcomeConfiguration getWelcome() {
            return welcome;
        }

        public void setWelcome(WelcomeConfiguration welcome) {
            this.welcome = welcome;
        }
    }

    private MainConfiguration config;

    private Config() {
        loadConfig(DEFAULT_CONFIG_PATH);
    }

    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public MainConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        saveConfig(DEFAULT_CONFIG_PATH);
    }

    public void saveConfig(String filePath) {
        var configFile = Paths.get(filePath);
        YamlConfigurations.save(configFile, MainConfiguration.class, config);
    }

    public void loadConfig(String filePath) {
        var configFile = Paths.get(filePath);
        if (Files.exists(configFile)) {
            this.config = YamlConfigurations.load(configFile, MainConfiguration.class);
        } else {
            this.config = new MainConfiguration();
            saveConfig(filePath);
        }
    }
}

