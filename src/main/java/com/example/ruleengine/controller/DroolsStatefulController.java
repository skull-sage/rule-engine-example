package com.example.ruleengine.controller;

import com.example.ruleengine.model.ExecutionSummary;
import com.example.ruleengine.model.ShoppingCart;
import com.example.ruleengine.service.StatefulDroolsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoint demonstrating Stateful KieSession.
 */
@RestController
@RequestMapping("/api/drools/stateful")
public class DroolsStatefulController {

    private final StatefulDroolsService statefulService;

    public DroolsStatefulController(StatefulDroolsService statefulService) {
        this.statefulService = statefulService;
    }

    @PostMapping("/cart-evaluate")
    public ResponseEntity<ExecutionSummary<ShoppingCart>> evaluateCart(@RequestBody ShoppingCart cart) {
        ExecutionSummary<ShoppingCart> summary = statefulService.evaluateShoppingCart(cart);
        return ResponseEntity.ok(summary);
    }
}
