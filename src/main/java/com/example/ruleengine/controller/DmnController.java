package com.example.ruleengine.controller;

import com.example.ruleengine.model.*;
import com.example.ruleengine.service.DmnEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints demonstrating KIE DMN Evaluation.
 */
@RestController
@RequestMapping("/api/dmn")
public class DmnController {

    private final DmnEvaluationService dmnEvaluationService;

    public DmnController(DmnEvaluationService dmnEvaluationService) {
        this.dmnEvaluationService = dmnEvaluationService;
    }

    @PostMapping("/loan-eligibility")
    public ResponseEntity<ExecutionSummary<LoanDecision>> evaluateLoanEligibility(@RequestBody LoanApplication application) {
        ExecutionSummary<LoanDecision> summary = dmnEvaluationService.evaluateLoanEligibility(application);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/insurance-quote")
    public ResponseEntity<ExecutionSummary<InsuranceQuoteDecision>> evaluateInsuranceQuote(@RequestBody InsuranceQuoteRequest request) {
        ExecutionSummary<InsuranceQuoteDecision> summary = dmnEvaluationService.evaluateInsuranceQuote(request);
        return ResponseEntity.ok(summary);
    }
}

