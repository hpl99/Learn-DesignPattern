package com.savebite.patterns.behavioral.strategy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

import java.time.Duration;
import java.time.Instant;

// Pattern: Strategy
// Category: Behavioral
// Problem it solves here: Concrete discounting strategy dynamically computing markdown based on remaining shelf life: >4h = 25%, 2-4h = 50%, <2h = 75%.
// GoF participant role: ConcreteStrategy
// Used by: PricingContext
@DesignPattern(name = "Strategy", role = "ConcreteStrategy", description = "Dynamic expiry-proximity discount algorithm")
public class ExpiryBasedDiscountStrategy implements DiscountStrategy {

    @Override
    public String getStrategyName() {
        return "Expiry-Based Dynamic Discount";
    }

    @Override
    public double getDiscountPercentage(Listing listing) {
        if (listing.getExpiryTime() == null) return 30.0;
        long minutesLeft = Duration.between(Instant.now(), listing.getExpiryTime()).toMinutes();

        if (minutesLeft <= 120) {
            return 75.0; // Last chance urgent clearance
        } else if (minutesLeft <= 240) {
            return 50.0; // Moderate evening markdown
        } else {
            return 25.0; // Early surplus arrival
        }
    }

    @Override
    public double calculateDiscountedPrice(Listing listing) {
        double pct = getDiscountPercentage(listing);
        double discounted = listing.getOriginalPrice() * (1.0 - (pct / 100.0));
        double price = Math.round(discounted * 100.0) / 100.0;

        PatternTracer.getInstance().record(
            "Strategy",
            "ExpiryBasedDiscountStrategy",
            "calculateDiscountedPrice",
            "Computed " + (int)pct + "% expiry discount on '" + listing.getTitle() + "' -> ₹" + price
        );

        return price;
    }

    @Override
    public String getExplanation(Listing listing) {
        long minutesLeft = listing.getExpiryTime() != null ? Duration.between(Instant.now(), listing.getExpiryTime()).toMinutes() : 180;
        return String.format("Expiry in %d mins -> %.0f%% off original price", minutesLeft, getDiscountPercentage(listing));
    }
}

