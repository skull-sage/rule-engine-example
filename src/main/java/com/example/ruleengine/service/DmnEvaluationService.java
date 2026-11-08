package com.example.ruleengine.service;

import com.example.ruleengine.model.*;
import org.kie.dmn.api.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service demonstrating KIE DMN (Decision Model and Notation) evaluation.
 *
 * Characteristics:
 * - Declarative, visual standard (OMG DMN specification).
 * - Business-friendly decision tables and FEEL expressions.
 * - Stateless evaluation via DMNRuntime and DMNContext.
 * - Standardized decision evaluation without writing complex procedural code.
 */
@Service
public class DmnEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(DmnEvaluationService.class);

    private static final String LOAN_NAMESPACE = "https://example.com/dmn/loan-eligibility";
    private static final String LOAN_MODEL_NAME = "LoanEligibility";
    private static final String LOAN_DECISION_NAME = "Loan Eligibility Decision";

    private static final String INSURANCE_NAMESPACE = "https://example.com/dmn/insurance-pricing";
    private static final String INSURANCE_MODEL_NAME = "InsurancePricing";
    private static final String INSURANCE_DECISION_NAME = "Insurance Quote Decision";

    private final DMNRuntime dmnRuntime;

    public DmnEvaluationService(DMNRuntime dmnRuntime) {
        this.dmnRuntime = dmnRuntime;
    }

    public ExecutionSummary<LoanDecision> evaluateLoanEligibility(LoanApplication application) {
        long startTime = System.nanoTime();

        DMNModel dmnModel = dmnRuntime.getModel(LOAN_NAMESPACE, LOAN_MODEL_NAME);
        if (dmnModel == null) {
            throw new IllegalStateException("DMN Model not found: " + LOAN_NAMESPACE + " / " + LOAN_MODEL_NAME);
        }

        DMNContext context = dmnRuntime.newContext();
        context.set("age", application.getAge());
        context.set("monthlyIncome", application.getMonthlyIncome());
        context.set("creditScore", application.getCreditScore());
        context.set("requestedAmount", application.getRequestedAmount());

        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, context);

        if (dmnResult.hasErrors()) {
            log.error("DMN evaluation produced errors: {}", dmnResult.getMessages());
            throw new IllegalStateException("DMN evaluation errors: " + dmnResult.getMessages());
        }

        DMNDecisionResult decisionResult = dmnResult.getDecisionResultByName(LOAN_DECISION_NAME);
        if (decisionResult == null || decisionResult.getResult() == null) {
            throw new IllegalStateException("No decision returned for: " + LOAN_DECISION_NAME);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> outputMap = (Map<String, Object>) decisionResult.getResult();

        LoanDecision decision = new LoanDecision();
        decision.setApproved(Boolean.TRUE.equals(outputMap.get("approved")));
        decision.setMaxApprovedAmount(toDouble(outputMap.get("maxApprovedAmount")));
        decision.setRiskGrade((String) outputMap.get("riskGrade"));
        decision.setInterestRate(toDouble(outputMap.get("interestRate")));
        decision.setReasons(Collections.singletonList((String) outputMap.get("reason")));

        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        return new ExecutionSummary<>(
                "DMN",
                LOAN_MODEL_NAME + " (" + LOAN_DECISION_NAME + ")",
                durationMs,
                Collections.singletonList("Decision Table: " + LOAN_DECISION_NAME),
                decision
        );
    }

    public ExecutionSummary<InsuranceQuoteDecision> evaluateInsuranceQuote(InsuranceQuoteRequest request) {
        long startTime = System.nanoTime();

        DMNModel dmnModel = dmnRuntime.getModel(INSURANCE_NAMESPACE, INSURANCE_MODEL_NAME);
        if (dmnModel == null) {
            throw new IllegalStateException("DMN Model not found: " + INSURANCE_NAMESPACE + " / " + INSURANCE_MODEL_NAME);
        }

        DMNContext context = dmnRuntime.newContext();
        context.set("driverAge", request.getDriverAge());
        context.set("accidentCount", request.getAccidentCount());
        context.set("vehicleType", request.getVehicleType());

        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, context);

        if (dmnResult.hasErrors()) {
            log.error("DMN insurance evaluation errors: {}", dmnResult.getMessages());
            throw new IllegalStateException("DMN insurance evaluation errors: " + dmnResult.getMessages());
        }

        DMNDecisionResult decisionResult = dmnResult.getDecisionResultByName(INSURANCE_DECISION_NAME);
        if (decisionResult == null || decisionResult.getResult() == null) {
            throw new IllegalStateException("No decision returned for: " + INSURANCE_DECISION_NAME);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> outputMap = (Map<String, Object>) decisionResult.getResult();

        InsuranceQuoteDecision quote = new InsuranceQuoteDecision();
        quote.setEligible(Boolean.TRUE.equals(outputMap.get("eligible")));
        quote.setBasePremium(toDouble(outputMap.get("basePremium")));
        quote.setRiskMultiplier(toDouble(outputMap.get("riskMultiplier")));
        quote.setFinalPremium(toDouble(outputMap.get("finalPremium")));
        quote.setRiskCategory((String) outputMap.get("riskCategory"));
        quote.setExplanation((String) outputMap.get("explanation"));

        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        return new ExecutionSummary<>(
                "DMN",
                INSURANCE_MODEL_NAME + " (" + INSURANCE_DECISION_NAME + ")",
                durationMs,
                Collections.singletonList("Decision Table: " + INSURANCE_DECISION_NAME),
                quote
        );
    }

    private double toDouble(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        return 0.0;
    }
}
