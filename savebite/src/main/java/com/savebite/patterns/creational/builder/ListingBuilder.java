package com.savebite.patterns.creational.builder;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

import java.time.Instant;
import java.util.UUID;

// Pattern: Builder
// Category: Creational
// Problem it solves here: Food listings have numerous mandatory (title, seller, price, expiry) and optional parameters (photos, custom descriptions, initial state, weight). Telescoping constructors are unreadable and error-prone.
// GoF participant role: Builder
// Used by: SellerController, DemoDataSeeder
@DesignPattern(name = "Builder", role = "Builder", description = "Fluent builder for complex Listing domain entities")
public class ListingBuilder {
    private String id = UUID.randomUUID().toString();
    private String sellerId;
    private String sellerName = "Verified Baker/Kitchen";
    private String title;
    private String description = "Freshly prepared surplus edible food item.";
    private String category = "bakery";
    private double originalPrice;
    private double currentPrice;
    private int quantity = 1;
    private Instant expiryTime;
    private String state = "FRESH";
    private String photoUrl = "/img/placeholder.svg";
    private double weightKg = 0.5;
    private Instant createdAt = Instant.now();

    public ListingBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ListingBuilder withSeller(String sellerId, String sellerName) {
        this.sellerId = sellerId;
        if (sellerName != null && !sellerName.isBlank()) {
            this.sellerName = sellerName;
        }
        return this;
    }

    public ListingBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ListingBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ListingBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public ListingBuilder withPricing(double originalPrice, double currentPrice) {
        this.originalPrice = originalPrice;
        this.currentPrice = currentPrice;
        return this;
    }

    public ListingBuilder withOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
        if (this.currentPrice == 0) {
            this.currentPrice = originalPrice;
        }
        return this;
    }

    public ListingBuilder withCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        return this;
    }

    public ListingBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public ListingBuilder withExpiry(Instant expiryTime) {
        this.expiryTime = expiryTime;
        return this;
    }

    public ListingBuilder withState(String state) {
        this.state = state;
        return this;
    }

    public ListingBuilder withPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    public ListingBuilder withWeightKg(double weightKg) {
        this.weightKg = weightKg;
        return this;
    }

    public ListingBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Listing build() {
        if (title == null || title.isBlank()) {
            throw new IllegalStateException("Listing title is required.");
        }
        if (originalPrice <= 0) {
            throw new IllegalStateException("Original price must be positive.");
        }
        if (currentPrice <= 0) {
            currentPrice = originalPrice;
        }
        if (expiryTime == null) {
            expiryTime = Instant.now().plusSeconds(3600 * 4); // Default 4 hours from now
        }

        Listing listing = new Listing(
            id, sellerId, sellerName, title, description,
            category, originalPrice, currentPrice, quantity,
            expiryTime, state, photoUrl, weightKg, createdAt
        );

        PatternTracer.getInstance().record(
            "Builder",
            "ListingBuilder",
            "build",
            "Constructed Listing: '" + title + "' (₹" + currentPrice + ", " + weightKg + "kg)"
        );

        return listing;
    }
}

