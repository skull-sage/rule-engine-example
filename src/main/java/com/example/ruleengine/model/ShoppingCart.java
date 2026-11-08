package com.example.ruleengine.model;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private String cartId;
    private String customerId;
    private List<CartItem> items = new ArrayList<>();
    private List<FreeGift> freeGifts = new ArrayList<>();
    private double originalTotal;
    private double discountTotal;
    private double finalTotal;
    private List<String> appliedPromotions = new ArrayList<>();

    public ShoppingCart() {
    }

    public ShoppingCart(String cartId, String customerId) {
        this.cartId = cartId;
        this.customerId = customerId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public void addItem(CartItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    public List<FreeGift> getFreeGifts() {
        return freeGifts;
    }

    public void setFreeGifts(List<FreeGift> freeGifts) {
        this.freeGifts = freeGifts;
    }

    public void addFreeGift(FreeGift gift) {
        if (this.freeGifts == null) {
            this.freeGifts = new ArrayList<>();
        }
        this.freeGifts.add(gift);
    }

    public double getOriginalTotal() {
        return originalTotal;
    }

    public void setOriginalTotal(double originalTotal) {
        this.originalTotal = originalTotal;
    }

    public double getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(double discountTotal) {
        this.discountTotal = discountTotal;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public List<String> getAppliedPromotions() {
        return appliedPromotions;
    }

    public void setAppliedPromotions(List<String> appliedPromotions) {
        this.appliedPromotions = appliedPromotions;
    }

    public void addPromotion(String promo) {
        if (this.appliedPromotions == null) {
            this.appliedPromotions = new ArrayList<>();
        }
        this.appliedPromotions.add(promo);
    }

    public void recalculateTotals() {
        double subtotal = 0.0;
        double itemDiscounts = 0.0;
        if (items != null) {
            for (CartItem item : items) {
                subtotal += (item.getUnitPrice() * item.getQuantity());
                itemDiscounts += item.getDiscount();
            }
        }
        this.originalTotal = subtotal;
        this.discountTotal = itemDiscounts;
        this.finalTotal = Math.max(0.0, originalTotal - discountTotal);
    }
}

