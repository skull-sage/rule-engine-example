package com.example.ruleengine.model;

public class Discount {
    private double percentage;
    private double discountAmount;
    private int loyaltyPointsAwarded;
    private boolean freeShipping;
    private String description;

    public Discount() {
    }

    public Discount(double percentage, double discountAmount, int loyaltyPointsAwarded, boolean freeShipping, String description) {
        this.percentage = percentage;
        this.discountAmount = discountAmount;
        this.loyaltyPointsAwarded = loyaltyPointsAwarded;
        this.freeShipping = freeShipping;
        this.description = description;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getLoyaltyPointsAwarded() {
        return loyaltyPointsAwarded;
    }

    public void setLoyaltyPointsAwarded(int loyaltyPointsAwarded) {
        this.loyaltyPointsAwarded = loyaltyPointsAwarded;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
