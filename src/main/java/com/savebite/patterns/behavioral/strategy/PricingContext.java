package com.savebite.patterns.behavioral.strategy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

// Pattern: Strategy
// Category: Behavioral
// Problem it solves here: Context class that holds a reference to a DiscountStrategy and can swap algorithms dynamically at runtime.
// GoF participant role: Context
// Used by: ListingService, PricingController
@DesignPattern(name = "Strategy", role = "Context", description = "Context delegating calculation to configured DiscountStrategy")
public class PricingContext {
    private DiscountStrategy strategy;

    public PricingContext(DiscountStrategy initialStrategy) {
        this.strategy = initialStrategy != null ? initialStrategy : new ExpiryBasedDiscountStrategy();
    }

    public void setStrategy(DiscountStrategy strategy) {
        PatternTracer.getInstance().record(
            "Strategy",
            "PricingContext",
            "setStrategy",
            "Swapped active pricing strategy to: " + strategy.getStrategyName()
        );
        this.strategy = strategy;
    }

    public DiscountStrategy getStrategy() {
        return strategy;
    }

    public double calculatePrice(Listing listing) {
        return strategy.calculateDiscountedPrice(listing);
    }

    public double getDiscountPercentage(Listing listing) {
        return strategy.getDiscountPercentage(listing);
    }
}

