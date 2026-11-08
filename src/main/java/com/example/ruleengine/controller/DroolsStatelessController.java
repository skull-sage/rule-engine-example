package com.example.ruleengine.controller;

import com.example.ruleengine.model.ExecutionSummary;
import com.example.ruleengine.model.Order;
import com.example.ruleengine.service.StatelessDroolsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoint demonstrating StatelessKieSession.
 */
@RestController
@RequestMapping("/api/drools/stateless")
public class DroolsStatelessController {

    private final StatelessDroolsService statelessService;

    public DroolsStatelessController(StatelessDroolsService statelessService) {
        this.statelessService = statelessService;
    }

    @PostMapping("/discount")
    public ResponseEntity<ExecutionSummary<Order>> evaluateDiscount(@RequestBody Order order) {
        ExecutionSummary<Order> summary = statelessService.evaluateOrderDiscount(order);
        return ResponseEntity.ok(summary);
    }
}

