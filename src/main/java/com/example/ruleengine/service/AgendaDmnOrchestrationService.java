package com.example.ruleengine.service;

import com.example.ruleengine.config.TrackingAgendaEventListener;
import com.example.ruleengine.model.DynamicEvaluationRequest;
import com.example.ruleengine.model.ExecutionSummary;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service demonstrating KieSession targeting specific DMN models via agenda groups.
 *
 * Characteristics:
 * - Uses KieSession.getAgenda().getAgendaGroup(group).setFocus() to target specific rule phases.
 * - Injects DynamicDmnService as a Drools global so rules can invoke DMN evaluations on demand.
 * - Bridges DMN results into Working Memory as DmnDecisionFact.
 * - Enables Drools rules to apply post-processing, overrides, or audit tags.
 */
@Service
public class AgendaDmnOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(AgendaDmnOrchestrationService.class);

    private final KieContainer kieContainer;
    private final DynamicDmnService dynamicDmnService;

    public AgendaDmnOrchestrationService(KieContainer kieContainer, DynamicDmnService dynamicDmnService) {
        this.kieContainer = kieContainer;
        this.dynamicDmnService = dynamicDmnService;
    }

    public ExecutionSummary<DynamicEvaluationRequest> evaluateAgendaDmn(DynamicEvaluationRequest request) {
        long startTime = System.nanoTime();

        String targetAgenda = (request.getAgendaGroup() != null && !request.getAgendaGroup().isBlank())
                ? request.getAgendaGroup()
                : "dynamic-dmn-runner";
        request.setAgendaGroup(targetAgenda);

        KieSession kieSession = kieContainer.newKieSession();
        TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        kieSession.addEventListener(listener);

        try {
            // Inject the DynamicDmnService as a global variable into the KieSession
            kieSession.setGlobal("dynamicDmnService", dynamicDmnService);

            // Insert the evaluation request fact
            kieSession.insert(request);

            // Target the specific agenda group using setFocus()
            log.debug("Setting agenda group focus to: '{}'", targetAgenda);
            kieSession.getAgenda().getAgendaGroup(targetAgenda).setFocus();

            // Fire rules exclusively in the targeted agenda group
            int rulesFired = kieSession.fireAllRules();
            log.debug("Agenda group '{}' executed. Rules fired: {}", targetAgenda, rulesFired);

            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            return new ExecutionSummary<>(
                    "HYBRID_AGENDA_DMN",
                    "AgendaGroup: " + targetAgenda + " -> Model: " + request.getTargetModelId(),
                    durationMs,
                    listener.getFiredRules(),
                    request
            );
        } finally {
            kieSession.dispose();
        }
    }

    public List<String> getAvailableAgendaGroups() {
        return List.of(
                "dynamic-dmn-runner",
                "loan-approval-orchestration",
                "retail-pricing-orchestration"
        );
    }
}
