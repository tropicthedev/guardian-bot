package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class Interview {
    private int interviewId;
    private int applicationId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Interview() {}

    public Interview(int interviewId, int applicationId) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public int getInterviewId() {
        return interviewId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
        this.modifiedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
}
