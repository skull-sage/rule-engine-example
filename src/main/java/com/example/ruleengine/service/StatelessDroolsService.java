package com.example.ruleengine.service;

import com.example.ruleengine.config.TrackingAgendaEventListener;
import com.example.ruleengine.model.Customer;
import com.example.ruleengine.model.ExecutionSummary;
import com.example.ruleengine.model.Order;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Service demonstrating StatelessKieSession.
 *
 * Characteristics:
 * - Thread-safe and lightweight.
 * - Stateless: Working memory is discarded immediately after execution.
 * - Ideal for pure decision functions (e.g., scoring, validation, price calculation).
 */
@Service
public class StatelessDroolsService {

    private static final Logger log = LoggerFactory.getLogger(StatelessDroolsService.class);
    private final KieContainer kieContainer;

    public StatelessDroolsService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public ExecutionSummary<Order> evaluateOrderDiscount(Order order) {
        long startTime = System.nanoTime();

        StatelessKieSession statelessSession = kieContainer.newStatelessKieSession();
        TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        statelessSession.addEventListener(listener);

        log.debug("Executing stateless rules for Order ID: {}", order.getOrderId());

        // Pass both Order and Customer (if present) into the stateless execution
        if (order.getCustomer() != null) {
            statelessSession.execute(Arrays.asList(order, order.getCustomer()));
        } else {
            statelessSession.execute(order);
        }

        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        return new ExecutionSummary<>(
                "DROOLS_STATELESS",
                "StatelessKieSession",
                durationMs,
                listener.getFiredRules(),
                order
        );
    }
}
