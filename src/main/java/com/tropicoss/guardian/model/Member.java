package com.tropicoss.guardian.model;

import com.tropicoss.guardian.utils.PlayerInfoFetcher;

public class Member {
    private String discordId;
    private String mojangId;
    private int isAdmin;
    private int onVacation;
    private String createdAt;
    private String modifiedAt;

    public Member() {}

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getMojangId() {
        return mojangId;
    }

    public void setMojangId(String mojangId) {
        this.mojangId = mojangId;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getOnVacation() {
        return onVacation;
    }

    public void setOnVacation(int onVacation) {
        this.onVacation = onVacation;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public PlayerInfoFetcher.Profile getPlayerProfile() {
        return PlayerInfoFetcher.getProfile(mojangId);
    }
}

