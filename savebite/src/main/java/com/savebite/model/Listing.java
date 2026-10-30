package com.savebite.model;

import java.time.Instant;

public class Listing {
    private String id;
    private String sellerId;
    private String sellerName;
    private String title;
    private String description;
    private String category;
    private double originalPrice;
    private double currentPrice;
    private int quantity;
    private Instant expiryTime;
    private String state; // FRESH, DISCOUNTED, LAST_CHANCE, EXPIRED, SOLD
    private String photoUrl;
    private double weightKg;
    private Instant createdAt;

    public Listing() {}

    public Listing(String id, String sellerId, String sellerName, String title, String description,
                   String category, double originalPrice, double currentPrice, int quantity,
                   Instant expiryTime, String state, String photoUrl, double weightKg, Instant createdAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.originalPrice = originalPrice;
        this.currentPrice = currentPrice;
        this.quantity = quantity;
        this.expiryTime = expiryTime;
        this.state = state;
        this.photoUrl = photoUrl;
        this.weightKg = weightKg;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Instant getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Instant expiryTime) { this.expiryTime = expiryTime; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

