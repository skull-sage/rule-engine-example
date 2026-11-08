package com.example.ruleengine.model;

import java.util.ArrayList;
import java.util.List;

public class LoanApplication {
    private String applicantId;
    private String applicantName;
    private int age;
    private double monthlyIncome;
    private int creditScore;
    private double requestedAmount;
    private int employmentMonths;

    public LoanApplication() {
    }

    public LoanApplication(String applicantId, String applicantName, int age, double monthlyIncome, int creditScore, double requestedAmount, int employmentMonths) {
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.age = age;
        this.monthlyIncome = monthlyIncome;
        this.creditScore = creditScore;
        this.requestedAmount = requestedAmount;
        this.employmentMonths = employmentMonths;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public int getEmploymentMonths() {
        return employmentMonths;
    }

    public void setEmploymentMonths(int employmentMonths) {
        this.employmentMonths = employmentMonths;
    }
}
