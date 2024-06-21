package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class MojangAccount {
    private int mojangAccountId;
    private int memberId;
    private int mojangId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public MojangAccount() {}

    public MojangAccount(int mojangAccountId, int memberId, int mojangId) {
        this.mojangAccountId = mojangAccountId;
        this.memberId = memberId;
        this.mojangId = mojangId;
    }

    public int getMojangAccountId() {
        return mojangAccountId;
    }

    public int getMemberId() {
        return memberId;
    }

    public int getMojangId() {
        return mojangId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setMojangAccountId(int mojangAccountId) {
        this.mojangAccountId = mojangAccountId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setMojangId(int mojangId) {
        this.mojangId = mojangId;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
