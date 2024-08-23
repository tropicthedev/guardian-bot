package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class ApplicationResponse {
    private long applicationResponseId;
    private long adminId;
    private long applicationId;
    private String content;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ApplicationResponse() {
    }

    public ApplicationResponse(long adminId, long applicationId, String content, Status status) {
        this.adminId = adminId;
        this.applicationId = applicationId;
        this.content = content;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public long getApplicationResponseId() {
        return applicationResponseId;
    }

    public void setApplicationResponseId(long applicationResponseId) {
        this.applicationResponseId = applicationResponseId;
        this.modifiedAt = LocalDateTime.now();
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
        this.modifiedAt = LocalDateTime.now();
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
        this.modifiedAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
