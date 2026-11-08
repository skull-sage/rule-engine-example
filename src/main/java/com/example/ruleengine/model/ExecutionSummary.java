package com.example.ruleengine.model;

import java.util.ArrayList;
import java.util.List;

public class ExecutionSummary<T> {
    private String engineType; // DROOLS_STATELESS, DROOLS_STATEFUL, DMN
    private String sessionOrModel;
    private long executionDurationMs;
    private List<String> rulesFired = new ArrayList<>();
    private T result;

    public ExecutionSummary() {
    }

    public ExecutionSummary(String engineType, String sessionOrModel, long executionDurationMs, List<String> rulesFired, T result) {
        this.engineType = engineType;
        this.sessionOrModel = sessionOrModel;
        this.executionDurationMs = executionDurationMs;
        this.rulesFired = rulesFired;
        this.result = result;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getSessionOrModel() {
        return sessionOrModel;
    }

    public void setSessionOrModel(String sessionOrModel) {
        this.sessionOrModel = sessionOrModel;
    }

    public long getExecutionDurationMs() {
        return executionDurationMs;
    }

    public void setExecutionDurationMs(long executionDurationMs) {
        this.executionDurationMs = executionDurationMs;
    }

    public List<String> getRulesFired() {
        return rulesFired;
    }

    public void setRulesFired(List<String> rulesFired) {
        this.rulesFired = rulesFired;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
