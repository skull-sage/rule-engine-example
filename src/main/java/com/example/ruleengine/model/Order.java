package com.example.ruleengine.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private Customer customer;
    private double orderAmount;
    private Discount discount = new Discount();
    private List<String> appliedRules = new ArrayList<>();

    public Order() {
    }

    public Order(String orderId, Customer customer, double orderAmount) {
        this.orderId = orderId;
        this.customer = customer;
        this.orderAmount = orderAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public List<String> getAppliedRules() {
        return appliedRules;
    }

    public void setAppliedRules(List<String> appliedRules) {
        this.appliedRules = appliedRules;
    }

    public void addAppliedRule(String ruleName) {
        if (this.appliedRules == null) {
            this.appliedRules = new ArrayList<>();
        }
        this.appliedRules.add(ruleName);
    }
}
