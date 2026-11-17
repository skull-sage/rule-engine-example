package com.example.ruleengine.model;

public class DmnRegistrationRequest {
    private String modelId;
    private String dmnXml;
    private String description;

    public DmnRegistrationRequest() {
    }

    public DmnRegistrationRequest(String modelId, String dmnXml, String description) {
        this.modelId = modelId;
        this.dmnXml = dmnXml;
        this.description = description;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getDmnXml() {
        return dmnXml;
    }

    public void setDmnXml(String dmnXml) {
        this.dmnXml = dmnXml;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
