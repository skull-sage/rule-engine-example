package com.example.ruleengine.model;

public class Customer {
    private String id;
    private String name;
    private String loyaltyTier; // REGULAR, SILVER, GOLD, PLATINUM
    private int loyaltyPoints;

    public Customer() {
    }

    public Customer(String id, String name, String loyaltyTier, int loyaltyPoints) {
        this.id = id;
        this.name = name;
        this.loyaltyTier = loyaltyTier;
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}

