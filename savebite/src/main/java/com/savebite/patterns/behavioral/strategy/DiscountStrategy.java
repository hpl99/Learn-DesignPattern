package com.savebite.patterns.behavioral.strategy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;

// Pattern: Strategy
// Category: Behavioral
// Problem it solves here: Sellers need flexible, interchangeable discounting algorithms. Expiry-based discounting increases discounts as closing time approaches, while Flat promo discounting applies fixed percentages, and Bulk buy discounts reward clearing large surplus batches. Strategy enables switching these algorithms at runtime without modifying listing classes.
// GoF participant role: Strategy
// Used by: ExpiryBasedDiscountStrategy, FlatDiscountStrategy, BulkBuyDiscountStrategy, PricingContext
@DesignPattern(name = "Strategy", role = "Strategy", description = "Base Strategy interface defining discount calculation")
public interface DiscountStrategy {
    String getStrategyName();
    double calculateDiscountedPrice(Listing listing);
    double getDiscountPercentage(Listing listing);
    String getExplanation(Listing listing);
}

