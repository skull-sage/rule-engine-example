package com.example.ruleengine;

import com.example.ruleengine.config.DroolsConfig;
import com.example.ruleengine.model.DynamicEvaluationRequest;
import com.example.ruleengine.model.ExecutionSummary;
import com.example.ruleengine.service.AgendaDmnOrchestrationService;
import com.example.ruleengine.service.DynamicDmnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AgendaDmnOrchestrationTest {

    private AgendaDmnOrchestrationService orchestrationService;
    private DynamicDmnService dynamicDmnService;

    @BeforeEach
    void setUp() {
        DroolsConfig config = new DroolsConfig();
        KieContainer container = config.kieContainer();
        dynamicDmnService = new DynamicDmnService();
        dynamicDmnService.initDefaultModels();
        orchestrationService = new AgendaDmnOrchestrationService(container, dynamicDmnService);
    }

    @Test
    @DisplayName("Target dynamic-dmn-runner agenda group to evaluate targeted DMN")
    void testTargetGenericAgendaGroup() {
        Map<String, Object> facts = Map.of(
                "driverAge", 30,
                "accidentCount", 0,
                "vehicleType", "SEDAN"
        );
        DynamicEvaluationRequest request = new DynamicEvaluationRequest(
                "dynamic-dmn-runner",
                "insurance-pricing",
                facts
        );

        ExecutionSummary<DynamicEvaluationRequest> summary = orchestrationService.evaluateAgendaDmn(request);

        assertNotNull(summary);
        assertEquals("HYBRID_AGENDA_DMN", summary.getEngineType());
        assertTrue(summary.getRulesFired().contains("Evaluate Targeted DMN via Generic Agenda"));

        DynamicEvaluationRequest result = summary.getResult();
        assertTrue(result.isExecuted());
        assertNotNull(result.getDecisionFact());
        assertEquals("insurance-pricing", result.getDecisionFact().getModelId());
        assertEquals(true, result.getDecisionFact().getValue("eligible"));
        assertEquals(600.0, ((Number) result.getDecisionFact().getValue("finalPremium")).doubleValue());
    }

    @Test
    @DisplayName("Target loan-approval-orchestration agenda group: Prime credit score triggers Drools VIP rate reduction")
    void testLoanOrchestrationWithVipOverride() {
        // Prime applicant with 820 credit score
        Map<String, Object> facts = Map.of(
                "age", 35,
                "monthlyIncome", 90000.0,
                "creditScore", 820,
                "requestedAmount", 200000.0
        );
        DynamicEvaluationRequest request = new DynamicEvaluationRequest(
                "loan-approval-orchestration",
                "loan-eligibility",
                facts
        );

        ExecutionSummary<DynamicEvaluationRequest> summary = orchestrationService.evaluateAgendaDmn(request);

        assertTrue(summary.getRulesFired().contains("Execute Loan Eligibility DMN in Agenda"));
        assertTrue(summary.getRulesFired().contains("High Credit Score Prime Rate Override"));

        DynamicEvaluationRequest result = summary.getResult();
        assertTrue(result.isOverrideApplied());

        // Base DMN rate was 6.5%, Drools override reduced it by 1.0% -> 5.5%
        assertEquals(5.5, ((Number) result.getDecisionFact().getValue("interestRate")).doubleValue());
        assertEquals(true, result.getDecisionFact().getValue("vipOverrideApplied"));
    }

    @Test
    @DisplayName("Target retail-pricing-orchestration agenda group attaches $25 seasonal loyalty voucher")
    void testRetailPricingOrchestrationEnrichment() {
        Map<String, Object> facts = Map.of(
                "driverAge", 28,
                "accidentCount", 0,
                "vehicleType", "SUV"
        );
        DynamicEvaluationRequest request = new DynamicEvaluationRequest(
                "retail-pricing-orchestration",
                "insurance-pricing",
                facts
        );

        ExecutionSummary<DynamicEvaluationRequest> summary = orchestrationService.evaluateAgendaDmn(request);

        assertTrue(summary.getRulesFired().contains("Holiday Loyalty Voucher Enrichment"));
        DynamicEvaluationRequest result = summary.getResult();
        assertEquals("$25 Holiday Gift Card", result.getDecisionFact().getValue("seasonalBonusVoucher"));
    }
}
