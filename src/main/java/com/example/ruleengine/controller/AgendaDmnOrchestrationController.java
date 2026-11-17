package com.example.ruleengine.controller;

import com.example.ruleengine.model.DynamicEvaluationRequest;
import com.example.ruleengine.model.ExecutionSummary;
import com.example.ruleengine.service.AgendaDmnOrchestrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller demonstrating KieSession evaluation targeting specific
 * DMN models via agenda groups and service function invocations.
 */
@RestController
@RequestMapping("/api/orchestration")
public class AgendaDmnOrchestrationController {

    private final AgendaDmnOrchestrationService orchestrationService;

    public AgendaDmnOrchestrationController(AgendaDmnOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    /**
     * Target a specific agenda group in a KieSession to evaluate requested facts
     * against a registered DMN model, allowing Drools rules to enrich or override the result.
     */
    @PostMapping("/agenda-dmn/evaluate")
    public ResponseEntity<ExecutionSummary<DynamicEvaluationRequest>> evaluateViaAgendaGroup(
            @RequestBody DynamicEvaluationRequest request) {
        ExecutionSummary<DynamicEvaluationRequest> summary = orchestrationService.evaluateAgendaDmn(request);
        return ResponseEntity.ok(summary);
    }

    /**
     * List all available agenda groups configured in the Drools rules.
     */
    @GetMapping("/agenda-groups")
    public ResponseEntity<Map<String, Object>> listAgendaGroups() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("availableAgendaGroups", orchestrationService.getAvailableAgendaGroups());
        response.put("descriptions", Map.of(
                "dynamic-dmn-runner", "Generic orchestrator that targets any registered DMN model specified in targetModelId.",
                "loan-approval-orchestration", "Evaluates loan DMN table and applies Drools VIP rate concession if creditScore >= 800.",
                "retail-pricing-orchestration", "Evaluates retail pricing DMN and attaches a $25 seasonal loyalty voucher bonus."
        ));
        return ResponseEntity.ok(response);
    }
}
