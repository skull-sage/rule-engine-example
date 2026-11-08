package com.example.ruleengine;

import com.example.ruleengine.config.DroolsConfig;
import com.example.ruleengine.model.*;
import com.example.ruleengine.service.DmnEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNRuntime;

import static org.junit.jupiter.api.Assertions.*;

class DmnEvaluationTest {

    private DmnEvaluationService dmnService;

    @BeforeEach
    void setUp() {
        DroolsConfig config = new DroolsConfig();
        KieContainer kieContainer = config.kieContainer();
        DMNRuntime dmnRuntime = config.dmnRuntime(kieContainer);
        dmnService = new DmnEvaluationService(dmnRuntime);
    }

    @Test
    @DisplayName("DMN Loan Evaluation: Prime Tier applicant approved with low risk rate")
    void testPrimeTierLoanApproval() {
        LoanApplication app = new LoanApplication("APP-001", "John Doe", 32, 75000.0, 780, 200000.0, 48);

        ExecutionSummary<LoanDecision> summary = dmnService.evaluateLoanEligibility(app);

        assertNotNull(summary);
        assertEquals("DMN", summary.getEngineType());

        LoanDecision decision = summary.getResult();
        assertTrue(decision.isApproved());
        assertEquals("LOW_RISK", decision.getRiskGrade());
        assertEquals(500000.0, decision.getMaxApprovedAmount());
        assertEquals(6.5, decision.getInterestRate());
    }

    @Test
    @DisplayName("DMN Loan Evaluation: Underage applicant (<18) is rejected")
    void testUnderageLoanRejection() {
        LoanApplication app = new LoanApplication("APP-002", "Minor Applicant", 17, 30000.0, 700, 10000.0, 12);

        ExecutionSummary<LoanDecision> summary = dmnService.evaluateLoanEligibility(app);
        LoanDecision decision = summary.getResult();

        assertFalse(decision.isApproved());
        assertEquals("DECLINED", decision.getRiskGrade());
        assertTrue(decision.getReasons().get(0).contains("at least 18 years old"));
    }

    @Test
    @DisplayName("DMN Loan Evaluation: Low credit score (<580) is rejected")
    void testLowCreditScoreRejection() {
        LoanApplication app = new LoanApplication("APP-003", "Risk Applicant", 29, 50000.0, 520, 20000.0, 24);

        ExecutionSummary<LoanDecision> summary = dmnService.evaluateLoanEligibility(app);
        LoanDecision decision = summary.getResult();

        assertFalse(decision.isApproved());
        assertEquals("DECLINED", decision.getRiskGrade());
        assertTrue(decision.getReasons().get(0).contains("below minimum requirement"));
    }

    @Test
    @DisplayName("DMN Insurance Evaluation: Mature driver with clean record receives preferred discount")
    void testInsurancePreferredDriver() {
        InsuranceQuoteRequest request = new InsuranceQuoteRequest("Sarah Connor", 35, 0, "SEDAN");

        ExecutionSummary<InsuranceQuoteDecision> summary = dmnService.evaluateInsuranceQuote(request);
        InsuranceQuoteDecision decision = summary.getResult();

        assertTrue(decision.isEligible());
        assertEquals("LOW_RISK", decision.getRiskCategory());
        assertEquals(600.0, decision.getBasePremium());
        assertEquals(1.0, decision.getRiskMultiplier());
        assertEquals(600.0, decision.getFinalPremium());
    }

    @Test
    @DisplayName("DMN Insurance Evaluation: Driver with >= 3 accidents is marked INELIGIBLE")
    void testInsuranceIneligibleDriver() {
        InsuranceQuoteRequest request = new InsuranceQuoteRequest("Risky Driver", 30, 4, "SEDAN");

        ExecutionSummary<InsuranceQuoteDecision> summary = dmnService.evaluateInsuranceQuote(request);
        InsuranceQuoteDecision decision = summary.getResult();

        assertFalse(decision.isEligible());
        assertEquals("INELIGIBLE", decision.getRiskCategory());
        assertEquals(0.0, decision.getFinalPremium());
    }
}

