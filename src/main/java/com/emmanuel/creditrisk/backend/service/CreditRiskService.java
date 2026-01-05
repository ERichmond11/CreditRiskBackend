package com.emmanuel.creditrisk.backend.service;

import com.emmanuel.creditrisk.backend.dto.CreditRiskRequest;
import com.emmanuel.creditrisk.backend.dto.CreditRiskResponse;
import com.emmanuel.creditrisk.backend.dto.CreditRiskStats;
import com.emmanuel.creditrisk.backend.entity.CreditApplication;
import com.emmanuel.creditrisk.backend.repository.CreditApplicationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditRiskService {

    private final CreditApplicationRepository repository;

    public CreditRiskService(CreditApplicationRepository repository) {
        this.repository = repository;
    }

    public CreditRiskResponse score(CreditRiskRequest req) {
        int creditScore = req.getCreditScore();
        int income = req.getAnnualIncome();
        int debt = req.getExistingDebt();
        int missed = req.getMissedPaymentsLast12Months();

        // Credit component (60%)
        double creditComponent = ((creditScore - 300.0) / 550.0) * 60.0;
        creditComponent = clamp(creditComponent, 0, 60);

        // Income component (20%)
        double incomeComponent = (Math.min(income, 120000) / 120000.0) * 20.0;
        incomeComponent = clamp(incomeComponent, 0, 20);

        // Debt penalty (up to 15%)
        double debtPenalty = (Math.min(debt, 80000) / 80000.0) * 15.0;

        // Missed payments penalty (up to 25%)
        double missedPenalty = Math.min(missed * 5.0, 25.0);

        double raw = creditComponent + incomeComponent - debtPenalty - missedPenalty;
        int score = (int) Math.round(clamp(raw, 0, 100));

        String riskLevel = score >= 70 ? "LOW" : score >= 50 ? "MEDIUM" : "HIGH";

        String decision;
        String reason;
        if (score >= 70 && missed <= 1) {
            decision = "APPROVE";
            reason = "Strong credit profile based on score + payment history.";
        } else if (score >= 50) {
            decision = "REVIEW";
            reason = "Borderline profile — manual review recommended.";
        } else {
            decision = "DECLINE";
            reason = "High risk based on scoring factors.";
        }

        // Securely get authenticated user's email
        String email = getAuthenticatedUserEmail();

        CreditApplication app = new CreditApplication();
        app.setUserEmail(email);
        app.setCreditScore(creditScore);
        app.setAnnualIncome(income);
        app.setExistingDebt(debt);
        app.setMissedPaymentsLast12Months(missed);
        app.setScore(score);
        app.setRiskLevel(riskLevel);
        app.setDecision(decision);
        app.setReason(reason);

        repository.save(app);

        return new CreditRiskResponse(score, riskLevel, decision, reason);
    }

    public List<CreditApplication> getHistory() {
        String email = getAuthenticatedUserEmail();
        return repository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    /**
     * Safely retrieves the authenticated user's email from the security context.
     * Throws a clear exception if authentication is missing or invalid.
     */
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String) {
            return (String) principal;
        }

        throw new IllegalStateException("Authenticated principal is not a valid email string");
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public CreditRiskStats getStats() {
        String email = getAuthenticatedUserEmail();
        List<CreditApplication> applications = repository.findByUserEmailOrderByCreatedAtDesc(email);

        CreditRiskStats stats = new CreditRiskStats();

        int total = applications.size();
        stats.setTotalApplications(total);

        if (total == 0) {
            stats.setAverageScore(0.0);
            stats.setApprovalRate(0.0);
            stats.setLastDecision("N/A");
            stats.setLastDate(null);
            return stats;
        }

        double avgScore = applications.stream()
                .mapToInt(CreditApplication::getScore)
                .average()
                .orElse(0.0);

        long approvedCount = applications.stream()
                .filter(app -> "APPROVE".equals(app.getDecision()))
                .count();

        double approvalRate = total > 0 ? (approvedCount * 100.0) / total : 0.0;

        CreditApplication latest = applications.get(0);

        stats.setAverageScore(Math.round(avgScore * 10.0) / 10.0);
        stats.setApprovalRate(Math.round(approvalRate * 10.0) / 10.0);
        stats.setLastDecision(latest.getDecision());
        stats.setLastDate(latest.getCreatedAt());

        return stats;
    }
}



