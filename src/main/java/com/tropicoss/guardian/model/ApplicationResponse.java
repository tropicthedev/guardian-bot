package com.tropicoss.guardian.model;

public class ApplicationResponse {
    private String applicationResponseId;
    private String adminId;
    private String applicationId;
    private String content;
    private String status;
    private String createdAt;
    private String modifiedAt;

    public ApplicationResponse() {
    }

    public String getApplicationResponseId() {
        return applicationResponseId;
    }

    public void setApplicationResponseId(String applicationResponseId) {
        this.applicationResponseId = applicationResponseId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
