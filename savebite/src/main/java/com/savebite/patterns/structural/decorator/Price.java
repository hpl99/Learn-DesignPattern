package com.savebite.patterns.structural.decorator;

import com.savebite.annotation.DesignPattern;

// Pattern: Decorator
// Category: Structural
// Problem it solves here: Dynamically attaches optional pricing modifiers (coupons, priority pickup, eco-friendly thermal packaging) without modifying the base food item or polluting its class hierarchy.
// GoF participant role: Component
// Used by: BasePrice, PriceDecorator, CheckoutFacade
@DesignPattern(name = "Decorator", role = "Component", description = "Base component interface for pricing calculations")
public interface Price {
    double getAmount();
    String getDescription();
}

