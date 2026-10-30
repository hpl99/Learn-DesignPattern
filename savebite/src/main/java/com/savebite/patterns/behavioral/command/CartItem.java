package com.savebite.patterns.behavioral.command;

public class CartItem {
    private final String listingId;
    private final String title;
    private final double unitPrice;
    private int quantity;
    private final double weightKg;

    public CartItem(String listingId, String title, double unitPrice, int quantity, double weightKg) {
        this.listingId = listingId;
        this.title = title;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.weightKg = weightKg;
    }

    public String getListingId() { return listingId; }
    public String getTitle() { return title; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getWeightKg() { return weightKg; }
    public double getSubtotal() { return unitPrice * quantity; }
}

