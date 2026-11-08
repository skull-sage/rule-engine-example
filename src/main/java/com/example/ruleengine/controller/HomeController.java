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
        overview.put("version", "1.0.0");

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

        return ResponseEntity.ok(overview);
    }
}
