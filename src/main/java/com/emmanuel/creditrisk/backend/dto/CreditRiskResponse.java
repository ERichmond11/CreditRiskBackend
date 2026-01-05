package com.emmanuel.creditrisk.backend.dto;

public class CreditRiskResponse {

    private int score;
    private String riskLevel;
    private String decision;
    private String reason;

    // Constructor matching what service returns
    public CreditRiskResponse(int score, String riskLevel, String decision, String reason) {
        this.score = score;
        this.riskLevel = riskLevel;
        this.decision = decision;
        this.reason = reason;
    }

    // Default constructor for JSON
    public CreditRiskResponse() {}

    // Getters
    public int getScore() {
        return score;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }
}