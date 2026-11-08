package com.example.ruleengine.model;

import java.util.ArrayList;
import java.util.List;

public class LoanDecision {
    private boolean approved;
    private double maxApprovedAmount;
    private String riskGrade; // LOW, MEDIUM, HIGH, DECLINED
    private double interestRate;
    private List<String> reasons = new ArrayList<>();

    public LoanDecision() {
    }

    public LoanDecision(boolean approved, double maxApprovedAmount, String riskGrade, double interestRate, List<String> reasons) {
        this.approved = approved;
        this.maxApprovedAmount = maxApprovedAmount;
        this.riskGrade = riskGrade;
        this.interestRate = interestRate;
        this.reasons = reasons;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public double getMaxApprovedAmount() {
        return maxApprovedAmount;
    }

    public void setMaxApprovedAmount(double maxApprovedAmount) {
        this.maxApprovedAmount = maxApprovedAmount;
    }

    public String getRiskGrade() {
        return riskGrade;
    }

    public void setRiskGrade(String riskGrade) {
        this.riskGrade = riskGrade;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public void addReason(String reason) {
        if (this.reasons == null) {
            this.reasons = new ArrayList<>();
        }
        this.reasons.add(reason);
    }
}
