package com.savebite.model;

public class OrderItem {
    private String id;
    private String orderId;
    private String listingId;
    private String title;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    private double weightKg;

    public OrderItem() {}

    public OrderItem(String id, String orderId, String listingId, String title, int quantity, double unitPrice, double subtotal, double weightKg) {
        this.id = id;
        this.orderId = orderId;
        this.listingId = listingId;
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
        this.weightKg = weightKg;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
}

