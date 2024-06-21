package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class ApplicationResponse {
    private int applicationResponseId;
    private int adminId;
    private int applicationId;
    private String content;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ApplicationResponse() {}

    public ApplicationResponse(int applicationResponseId, int adminId, int applicationId, String content, Status status){
        this.applicationResponseId = applicationResponseId;
        this.adminId = adminId;
        this.applicationId = applicationId;
        this.content = content;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public int getApplicationResponseId() {
        return applicationResponseId;
    }

    public int getAdminId() {
        return adminId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public String getContent() {
        return content;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setApplicationResponseId(int applicationResponseId) {
        this.applicationResponseId = applicationResponseId;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setContent(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setStatus(Status status) {
        this.status = status;
        this.modifiedAt = LocalDateTime.now();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
