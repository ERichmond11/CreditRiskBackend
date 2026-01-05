package com.emmanuel.creditrisk.backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "credit_applications",
        indexes = @Index(name = "idx_credit_app_user", columnList = "userEmail"))
public class CreditApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private int creditScore;
    private int annualIncome;
    private int existingDebt;
    private int missedPaymentsLast12Months;

    private int score;
    private String riskLevel;
    private String decision;

    @Column(length = 500)
    private String reason;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    // ---- constructors ----
    public CreditApplication() {}

    // ---- getters & setters ----

    public Long getId() { return id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }

    public int getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(int annualIncome) { this.annualIncome = annualIncome; }

    public int getExistingDebt() { return existingDebt; }
    public void setExistingDebt(int existingDebt) { this.existingDebt = existingDebt; }

    public int getMissedPaymentsLast12Months() { return missedPaymentsLast12Months; }
    public void setMissedPaymentsLast12Months(int missedPaymentsLast12Months) {
        this.missedPaymentsLast12Months = missedPaymentsLast12Months;
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}



