package com.tropicoss.guardian.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurations;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class Config {
    private static final String DEFAULT_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("guardian").resolve("config.yml").toString();
    private static Config instance;
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

    // Create the Server configuration
    @Configuration
    public static class ServerConfiguration {
        private boolean https = false;
        private String host = "127.0.0.1";
        private int port = 1234;
        @Comment("THIS CANNOT BE THE SAME AS PORT")
        private int websocketPort = 12345;

        public boolean getHttps() {
            return https;
        }

        public void setHttps(boolean https) {
            this.https = https;
        }

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

        public int getWebsocketPort() {
            return websocketPort;
        }

        public void setWebsocketPort(int websocketPort) {
            this.websocketPort = websocketPort;
        }
    }

    @Configuration
    public static class WelcomeConfiguration {
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
        private String clientId = "oauth-client-id";
        private String clientSecret = "oauth-client-secret";
        private String redirectUri = "oauth-redirect-uri";

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

        public void setGuild(String guild) {
            this.guild = guild;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
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

    @Configuration
    public static class MemberConfiguration {
        @Comment("Amount of time in days until someone is considered inactive")
        private int inactivityThreshold = 30;
        @Comment("Amount of time in days until someone has to login if a new joiner to not be purged for inactivity")
        private int gracePeriod = 7;
        @Comment("The frequency in which this check is done, use this website to determine that https://crontab.guru/")
        private String cron = "0 16 * * *";
        private String role = "role-id";
        @Comment("The channel that the welcome to the server message will be sent to")
        private String channel = "channel-id";
        private String message = "Welcome to the server {member}, ip is in <#channel-id>";
        private String inactivityMessage = "You are {x} days away from being removed for inactivity, please login or ask to be put in vacation mode if your admins allow it";
        private int notificationPeriod = 7;

        public int getInactivityThreshold() {
            return inactivityThreshold;
        }

        public void setInactivityThreshold(int inactivityThreshold) {
            this.inactivityThreshold = inactivityThreshold;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

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

        public int getGracePeriod() {
            return gracePeriod;
        }

        public void setGracePeriod(int gracePeriod) {
            this.gracePeriod = gracePeriod;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public String getInactivityMessage() {
            return inactivityMessage;
        }

        public void setInactivityMessage(String inactivityMessage) {
            this.inactivityMessage = inactivityMessage;
        }

        public int getNotificationPeriod() {
            return notificationPeriod;
        }

        public void setNotificationPeriod(int notificationPeriod) {
            this.notificationPeriod = notificationPeriod;
        }
    }

    @Configuration
    public static class ApplicationConfiguration {
        private int timeout = 5;
        private String channel = "channel-id";
        private List<String> questions = new ArrayList<>();
        @Comment("IMPORTANT!! Each item in this list must not be longer than 100 characters long or you will have issues with the embed")
        private List<String> denyReasons = new ArrayList<>();

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public List<String> getQuestions() {
            return questions;
        }

        public void setQuestions(List<String> questions) {
            this.questions = questions;
        }

        public List<String> getDenyReasons() {
            return denyReasons;
        }

        public void setDenyReasons(List<String> denyReasons) {
            this.denyReasons = denyReasons;
        }
    }

    @Configuration
    public static class InterviewConfiguration {
        private String role = "role-id";
        private String channel = "channel-id";
        private String message = "Hi {member}. This is a example message with a way to ping a member";

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

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

    // Main configuration class that combines the above configurations
    @Configuration
    public static class MainConfiguration {
        private ServerConfiguration server = new ServerConfiguration();
        private BotConfiguration bot = new BotConfiguration();
        private GenericConfiguration generic = new GenericConfiguration();
        private WelcomeConfiguration welcome = new WelcomeConfiguration();
        private ApplicationConfiguration application = new ApplicationConfiguration();
        private InterviewConfiguration interview = new InterviewConfiguration();
        private MemberConfiguration member = new MemberConfiguration();

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

        public ApplicationConfiguration getApplication() {
            return application;
        }

        public void setApplication(ApplicationConfiguration application) {
            this.application = application;
        }

        public InterviewConfiguration getInterview() {
            return interview;
        }

        public void setInterview(InterviewConfiguration interview) {
            this.interview = interview;
        }

        public MemberConfiguration getMember() {
            return member;
        }

        public void setMember(MemberConfiguration member) {
            this.member = member;
        }
    }
}

