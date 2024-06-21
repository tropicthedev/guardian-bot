package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class Server {
    private int serverId;
    private String name;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Server() {}

    public Server(int serverId, String name, String token) {
        this.serverId = serverId;
        this.name = name;
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public int getServerId() {
        return serverId;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setName(String name) {
        this.name = name;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setToken(String token) {
        this.token = token;
        this.modifiedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
