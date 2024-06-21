package com.tropicoss.guardian.database.model;

import java.time.LocalDateTime;

public class InterviewResponse {
    private int interviewResponseId;
    private int adminId;
    private int interviewId;
    private String content;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public InterviewResponse() {}

    public InterviewResponse(int interviewResponseId, int adminId, int interviewId, String content, Status status) {
        this.interviewResponseId = interviewResponseId;
        this.adminId = adminId;
        this.interviewId = interviewId;
        this.content = content;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public int getInterviewResponseId() {
        return interviewResponseId;
    }

    public int getAdminId() {
        return adminId;
    }

    public int getInterviewId() {
        return interviewId;
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

    public void setInterviewResponseId(int interviewResponseId) {
        this.interviewResponseId = interviewResponseId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
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
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}


