package com.emmanuel.creditrisk.backend.dto;

import java.time.OffsetDateTime;

public class CreditRiskStats {

    private long totalApplications;
    private double averageScore;
    private double approvalRate;
    private String lastDecision;
    private OffsetDateTime lastDate;

    public CreditRiskStats() {}

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getApprovalRate() {
        return approvalRate;
    }

    public void setApprovalRate(double approvalRate) {
        this.approvalRate = approvalRate;
    }

    public String getLastDecision() {
        return lastDecision;
    }

    public void setLastDecision(String lastDecision) {
        this.lastDecision = lastDecision;
    }

    public OffsetDateTime getLastDate() {
        return lastDate;
    }

    public void setLastDate(OffsetDateTime lastDate) {
        this.lastDate = lastDate;
    }
}

