package com.example.ruleengine.model;

public class InsuranceQuoteDecision {
    private boolean eligible;
    private double basePremium;
    private double riskMultiplier;
    private double finalPremium;
    private String riskCategory; // LOW_RISK, STANDARD_RISK, HIGH_RISK, INELIGIBLE
    private String explanation;

    public InsuranceQuoteDecision() {
    }

    public InsuranceQuoteDecision(boolean eligible, double basePremium, double riskMultiplier, double finalPremium, String riskCategory, String explanation) {
        this.eligible = eligible;
        this.basePremium = basePremium;
        this.riskMultiplier = riskMultiplier;
        this.finalPremium = finalPremium;
        this.riskCategory = riskCategory;
        this.explanation = explanation;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public double getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(double basePremium) {
        this.basePremium = basePremium;
    }

    public double getRiskMultiplier() {
        return riskMultiplier;
    }

    public void setRiskMultiplier(double riskMultiplier) {
        this.riskMultiplier = riskMultiplier;
    }

    public double getFinalPremium() {
        return finalPremium;
    }

    public void setFinalPremium(double finalPremium) {
        this.finalPremium = finalPremium;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}

