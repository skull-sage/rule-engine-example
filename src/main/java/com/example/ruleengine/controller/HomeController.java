package com.example.ruleengine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping(value = {"/", "/api"})
    public ResponseEntity<Map<String, Object>> getApiOverview() {
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("service", "Spring Boot Drools & DMN Rule Engine Examples");
        overview.put("status", "UP");
        overview.put("version", "1.1.0");

        Map<String, Object> droolsSection = new LinkedHashMap<>();
        droolsSection.put("stateless_discount_evaluation", Map.of(
                "method", "POST",
                "path", "/api/drools/stateless/discount",
                "concept", "Demonstrates StatelessKieSession, salience, fact matching with Order & Customer facts."
        ));
        droolsSection.put("stateful_cart_evaluation", Map.of(
                "method", "POST",
                "path", "/api/drools/stateful/cart-evaluate",
                "concept", "Demonstrates Stateful KieSession, working memory fact insertion, queries (GetFreeGifts), and session disposal."
        ));
        overview.put("drools_engine_examples", droolsSection);

        Map<String, Object> dmnSection = new LinkedHashMap<>();
        dmnSection.put("loan_eligibility_decision_table", Map.of(
                "method", "POST",
                "path", "/api/dmn/loan-eligibility",
                "concept", "Demonstrates DMN 1.4 Decision Table evaluation using DMNRuntime and DMNContext."
        ));
        dmnSection.put("insurance_quote_decision", Map.of(
                "method", "POST",
                "path", "/api/dmn/insurance-quote",
                "concept", "Demonstrates DMN multi-condition FEEL rules and tiered risk premium calculation."
        ));
        overview.put("dmn_engine_examples", dmnSection);

        Map<String, Object> dynamicDmnSection = new LinkedHashMap<>();
        dynamicDmnSection.put("register_dmn_json", Map.of(
                "method", "POST",
                "path", "/api/dmn/dynamic",
                "concept", "Dynamic DMN registration via JSON payload containing DMN XML."
        ));
        dynamicDmnSection.put("register_dmn_upload", Map.of(
                "method", "POST",
                "path", "/api/dmn/dynamic/upload",
                "concept", "Upload .dmn or .xml decision table exported from Excel / visual tools."
        ));
        dynamicDmnSection.put("list_registered_dmn_models", Map.of(
                "method", "GET",
                "path", "/api/dmn/dynamic",
                "concept", "List all actively registered and compiled DMN models."
        ));
        dynamicDmnSection.put("evaluate_dynamic_dmn_direct", Map.of(
                "method", "POST",
                "path", "/api/dmn/dynamic/{modelId}/evaluate",
                "concept", "Direct evaluation of any registered DMN with dynamic JSON inputs."
        ));
        overview.put("dynamic_dmn_management", dynamicDmnSection);

        Map<String, Object> orchestrationSection = new LinkedHashMap<>();
        orchestrationSection.put("evaluate_via_agenda_group", Map.of(
                "method", "POST",
                "path", "/api/orchestration/agenda-dmn/evaluate",
                "concept", "KieSession targets specific DMN via agenda group, invokes service function, and performs overrides/enrichments."
        ));
        orchestrationSection.put("list_agenda_groups", Map.of(
                "method", "GET",
                "path", "/api/orchestration/agenda-groups",
                "concept", "List all available Drools agenda groups and descriptions."
        ));
        overview.put("agenda_group_dmn_orchestration", orchestrationSection);

        return ResponseEntity.ok(overview);
    }
}
