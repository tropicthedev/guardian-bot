package com.tropicoss.guardian.model;

public class InterviewResponse {
    private String interviewResponseId;
    private String adminId;
    private String interviewId;
    private String content;
    private String status;
    private String createdAt;
    private String modifiedAt;

    public InterviewResponse() {
    }

    public String getInterviewResponseId() {
        return interviewResponseId;
    }

    public void setInterviewResponseId(String interviewResponseId) {
        this.interviewResponseId = interviewResponseId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(String interviewId) {
        this.interviewId = interviewId;
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

