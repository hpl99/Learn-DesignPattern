package com.savebite.patterns.behavioral.strategy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

// Pattern: Strategy
// Category: Behavioral
// Problem it solves here: Wholesale surplus clearance strategy: higher discounts for larger batches.
// GoF participant role: ConcreteStrategy
// Used by: PricingContext
@DesignPattern(name = "Strategy", role = "ConcreteStrategy", description = "Volume-based bulk clearance discount strategy")
public class BulkBuyDiscountStrategy implements DiscountStrategy {

    @Override
    public String getStrategyName() {
        return "Bulk Volume Clearance Strategy";
    }

    @Override
    public double getDiscountPercentage(Listing listing) {
        int qty = listing.getQuantity();
        if (qty >= 10) return 70.0;
        if (qty >= 5) return 50.0;
        return 30.0;
    }

    @Override
    public double calculateDiscountedPrice(Listing listing) {
        double pct = getDiscountPercentage(listing);
        double discounted = listing.getOriginalPrice() * (1.0 - (pct / 100.0));
        double price = Math.round(discounted * 100.0) / 100.0;

        PatternTracer.getInstance().record(
            "Strategy",
            "BulkBuyDiscountStrategy",
            "calculateDiscountedPrice",
            "Bulk discount for qty " + listing.getQuantity() + " (" + (int)pct + "%) on '" + listing.getTitle() + "' -> ₹" + price
        );

        return price;
    }

    @Override
    public String getExplanation(Listing listing) {
        return String.format("Stock %d units -> %.0f%% volume clearance discount", listing.getQuantity(), getDiscountPercentage(listing));
    }
}

