package com.example.ruleengine.service;

import com.example.ruleengine.config.TrackingAgendaEventListener;
import com.example.ruleengine.model.CartItem;
import com.example.ruleengine.model.ExecutionSummary;
import com.example.ruleengine.model.FreeGift;
import com.example.ruleengine.model.ShoppingCart;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service demonstrating Stateful KieSession.
 *
 * Characteristics:
 * - Maintains Working Memory across multiple interactions.
 * - Supports forward chaining (rules modifying facts or inserting new facts like FreeGift).
 * - Supports Kie queries over Working Memory.
 * - MUST be disposed after use (kieSession.dispose()) to release memory and resources.
 */
@Service
public class StatefulDroolsService {

    private static final Logger log = LoggerFactory.getLogger(StatefulDroolsService.class);
    private final KieContainer kieContainer;

    public StatefulDroolsService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public ExecutionSummary<ShoppingCart> evaluateShoppingCart(ShoppingCart cart) {
        long startTime = System.nanoTime();

        KieSession kieSession = kieContainer.newKieSession();
        TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        kieSession.addEventListener(listener);

        try {
            log.debug("Inserting ShoppingCart and items into stateful session");
            kieSession.insert(cart);

            if (cart.getItems() != null) {
                for (CartItem item : cart.getItems()) {
                    kieSession.insert(item);
                }
            }

            // Fire all activated rules until agenda is empty
            int rulesFiredCount = kieSession.fireAllRules();
            log.debug("Stateful session executed. Fired {} rule activations.", rulesFiredCount);

            // Execute a query to find all FreeGift facts inserted by rules during execution
            QueryResults results = kieSession.getQueryResults("GetFreeGifts");
            List<FreeGift> queriedGifts = new ArrayList<>();
            for (QueryResultsRow row : results) {
                FreeGift gift = (FreeGift) row.get("$gift");
                queriedGifts.add(gift);
            }
            cart.setFreeGifts(queriedGifts);

            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            return new ExecutionSummary<>(
                    "DROOLS_STATEFUL",
                    "KieSession (Stateful)",
                    durationMs,
                    listener.getFiredRules(),
                    cart
            );
        } finally {
            // Crucial: Always dispose of stateful sessions to prevent memory leaks!
            kieSession.dispose();
            log.debug("Stateful KieSession disposed.");
        }
    }
}

