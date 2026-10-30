package com.savebite.patterns.behavioral.strategy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

// Pattern: Strategy
// Category: Behavioral
// Problem it solves here: Applies flat rate promotional discount across all eligible items.
// GoF participant role: ConcreteStrategy
// Used by: PricingContext
@DesignPattern(name = "Strategy", role = "ConcreteStrategy", description = "Flat fixed percentage discount strategy")
public class FlatDiscountStrategy implements DiscountStrategy {
    private final double percentage;

    public FlatDiscountStrategy() {
        this(40.0);
    }

    public FlatDiscountStrategy(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String getStrategyName() {
        return "Flat Promotional Markdown (" + (int)percentage + "%)";
    }

    @Override
    public double getDiscountPercentage(Listing listing) {
        return percentage;
    }

    @Override
    public double calculateDiscountedPrice(Listing listing) {
        double discounted = listing.getOriginalPrice() * (1.0 - (percentage / 100.0));
        double price = Math.round(discounted * 100.0) / 100.0;

        PatternTracer.getInstance().record(
            "Strategy",
            "FlatDiscountStrategy",
            "calculateDiscountedPrice",
            "Flat " + (int)percentage + "% discount on '" + listing.getTitle() + "' -> ₹" + price
        );

        return price;
    }

    @Override
    public String getExplanation(Listing listing) {
        return "Flat promotional markdown: " + (int)percentage + "% off";
    }
}

