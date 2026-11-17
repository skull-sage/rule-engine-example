package com.example.ruleengine.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bridge fact representing the output of a DMN evaluation inside Drools Working Memory.
 * Drools rules can match on this fact, inspect decision outcomes, and apply further
 * business logic or overrides.
 */
public class DmnDecisionFact {

    private String modelId;
    private String decisionName;
    private Map<String, Object> decisionValues = new LinkedHashMap<>();
    private List<String> auditTags = new ArrayList<>();
    private boolean hasErrors;
    private List<String> errorMessages = new ArrayList<>();

    public DmnDecisionFact() {
    }

    public DmnDecisionFact(String modelId, String decisionName, Map<String, Object> decisionValues) {
        this.modelId = modelId;
        this.decisionName = decisionName;
        this.decisionValues = decisionValues;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getDecisionName() {
        return decisionName;
    }

    public void setDecisionName(String decisionName) {
        this.decisionName = decisionName;
    }

    public Map<String, Object> getDecisionValues() {
        return decisionValues;
    }

    public void setDecisionValues(Map<String, Object> decisionValues) {
        this.decisionValues = decisionValues;
    }

    public Object getValue(String key) {
        return decisionValues != null ? decisionValues.get(key) : null;
    }

    public void putValue(String key, Object value) {
        if (this.decisionValues == null) {
            this.decisionValues = new LinkedHashMap<>();
        }
        this.decisionValues.put(key, value);
    }

    public List<String> getAuditTags() {
        return auditTags;
    }

    public void setAuditTags(List<String> auditTags) {
        this.auditTags = auditTags;
    }

    public void addAuditTag(String tag) {
        if (this.auditTags == null) {
            this.auditTags = new ArrayList<>();
        }
        this.auditTags.add(tag);
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void addErrorMessage(String message) {
        if (this.errorMessages == null) {
            this.errorMessages = new ArrayList<>();
        }
        this.errorMessages.add(message);
        this.hasErrors = true;
    }
}
