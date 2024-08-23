package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class Application {
    private long applicationId;
    private String content;
    private String messageId;
    private String discordId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Application() {
    }

    public Application(String content, String messageId, String discordId) {
        this.content = content;
        this.messageId = messageId;
        this.discordId = discordId;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
        this.modifiedAt = LocalDateTime.now();
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
        this.modifiedAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}