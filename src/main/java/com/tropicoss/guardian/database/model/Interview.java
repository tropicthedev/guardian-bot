package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class Interview {
    private long interviewId;
    private long applicationId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Interview() {
    }

    public Interview(long interviewId, long applicationId) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
        this.modifiedAt = LocalDateTime.now();
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
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
