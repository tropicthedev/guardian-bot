package com.tropicoss.guardian.database.model;

import com.tropicoss.guardian.utils.PlayerInfoFetcher;

import java.time.LocalDateTime;

import static com.tropicoss.guardian.utils.PlayerInfoFetcher.*;

public class Member {
    private String memberId;
    private String discordId;
    private Boolean isAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Member() {
    }

    public Member(String memberId, String discordId, Boolean isAdmin) {
        this.memberId = memberId;
        this.discordId = discordId;
        this.isAdmin = isAdmin;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
        this.modifiedAt = LocalDateTime.now();
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
        this.modifiedAt = LocalDateTime.now();
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
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

    public Profile getPlayerProfile() {

        return getProfile(this.memberId);
    }
}
