package com.emmanuel.creditrisk.backend.controller;

import com.emmanuel.creditrisk.backend.dto.CreditRiskRequest;
import com.emmanuel.creditrisk.backend.dto.CreditRiskResponse;
import com.emmanuel.creditrisk.backend.dto.CreditRiskStats;
import com.emmanuel.creditrisk.backend.entity.CreditApplication;
import com.emmanuel.creditrisk.backend.service.CreditRiskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-risk")
@CrossOrigin(origins = "http://localhost:4200")  // Adjust if needed for production
public class CreditRiskController {

    private final CreditRiskService creditRiskService;

    public CreditRiskController(CreditRiskService creditRiskService) {
        this.creditRiskService = creditRiskService;
    }

    /**
     * Health check endpoint
     */
    @GetMapping
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("Credit Risk API running");
    }

    /**
     * Submit a new credit application and get scoring result
     */
    @PostMapping("/score")
    public ResponseEntity<CreditRiskResponse> score(@Valid @RequestBody CreditRiskRequest request) {
        CreditRiskResponse response = creditRiskService.score(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's application history
     */
    @GetMapping("/history")
    public ResponseEntity<List<CreditApplication>> history() {
        List<CreditApplication> history = creditRiskService.getHistory();
        return ResponseEntity.ok(history);
    }

    /**
     * Get user's summary statistics for dashboard
     */
    @GetMapping("/stats")
    public ResponseEntity<CreditRiskStats> getStats() {
        CreditRiskStats stats = creditRiskService.getStats();
        return ResponseEntity.ok(stats);
    }
}

