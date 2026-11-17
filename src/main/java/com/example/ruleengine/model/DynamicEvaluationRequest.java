package com.example.ruleengine.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DynamicEvaluationRequest {

    private String agendaGroup;
    private String targetModelId;
    private Map<String, Object> inputs = new LinkedHashMap<>();
    private boolean executed;
    private DmnDecisionFact decisionFact;
    private boolean overrideApplied;
    private List<String> executionTrace = new ArrayList<>();

    public DynamicEvaluationRequest() {
    }

    public DynamicEvaluationRequest(String agendaGroup, String targetModelId, Map<String, Object> inputs) {
        this.agendaGroup = agendaGroup;
        this.targetModelId = targetModelId;
        this.inputs = inputs;
    }

    public String getAgendaGroup() {
        return agendaGroup;
    }

    public void setAgendaGroup(String agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

    public String getTargetModelId() {
        return targetModelId;
    }

    public void setTargetModelId(String targetModelId) {
        this.targetModelId = targetModelId;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public DmnDecisionFact getDecisionFact() {
        return decisionFact;
    }

    public void setDecisionFact(DmnDecisionFact decisionFact) {
        this.decisionFact = decisionFact;
    }

    public boolean isOverrideApplied() {
        return overrideApplied;
    }

    public void setOverrideApplied(boolean overrideApplied) {
        this.overrideApplied = overrideApplied;
    }

    public List<String> getExecutionTrace() {
        return executionTrace;
    }

    public void setExecutionTrace(List<String> executionTrace) {
        this.executionTrace = executionTrace;
    }

    public void addTrace(String step) {
        if (this.executionTrace == null) {
            this.executionTrace = new ArrayList<>();
        }
        this.executionTrace.add(step);
    }
}
