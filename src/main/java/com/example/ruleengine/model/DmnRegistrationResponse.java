package com.example.ruleengine.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DmnRegistrationResponse {
    private String modelId;
    private String namespace;
    private String modelName;
    private List<String> decisions = new ArrayList<>();
    private List<String> inputs = new ArrayList<>();
    private long compileDurationMs;
    private String status; // SUCCESS, ERROR
    private String message;
    private LocalDateTime registeredAt;

    public DmnRegistrationResponse() {
    }

    public DmnRegistrationResponse(String modelId, String namespace, String modelName, List<String> decisions, List<String> inputs, long compileDurationMs, String status, String message) {
        this.modelId = modelId;
        this.namespace = namespace;
        this.modelName = modelName;
        this.decisions = decisions;
        this.inputs = inputs;
        this.compileDurationMs = compileDurationMs;
        this.status = status;
        this.message = message;
        this.registeredAt = LocalDateTime.now();
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<String> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<String> decisions) {
        this.decisions = decisions;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public long getCompileDurationMs() {
        return compileDurationMs;
    }

    public void setCompileDurationMs(long compileDurationMs) {
        this.compileDurationMs = compileDurationMs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
