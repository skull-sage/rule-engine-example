package com.example.ruleengine.config;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Listener that captures all rules fired during a KieSession execution.
 * Useful for debugging, audit trails, and returning rule execution provenance.
 */
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

    private static final Logger log = LoggerFactory.getLogger(TrackingAgendaEventListener.class);
    private final List<String> firedRules = new ArrayList<>();

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        String ruleName = event.getMatch().getRule().getName();
        firedRules.add(ruleName);
        log.debug("Rule fired: {}", ruleName);
    }

    public List<String> getFiredRules() {
        return Collections.unmodifiableList(firedRules);
    }

    public void reset() {
        firedRules.clear();
    }
}

