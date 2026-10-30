package com.savebite.patterns.structural.decorator;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Decorator
// Category: Structural
// Problem it solves here: The fundamental un-adorned food price before any add-ons or promotional adjustments.
// GoF participant role: ConcreteComponent
// Used by: PriceDecorator, CheckoutService
@DesignPattern(name = "Decorator", role = "ConcreteComponent", description = "Base food item price")
public class BasePrice implements Price {
    private final double amount;
    private final String label;

    public BasePrice(double amount) {
        this(amount, "Base Food Price");
    }

    public BasePrice(double amount, String label) {
        this.amount = Math.round(amount * 100.0) / 100.0;
        this.label = label;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public String getDescription() {
        return label + " (₹" + amount + ")";
    }
}

