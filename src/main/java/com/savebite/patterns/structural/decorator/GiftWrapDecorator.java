package com.savebite.patterns.structural.decorator;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Decorator
// Category: Structural
// Problem it solves here: Adds optional eco-friendly thermal insulated wrapping (+ ₹25) to preserve warm or chilled rescued bakery/dairy items.
// GoF participant role: ConcreteDecorator
// Used by: CheckoutFacade
@DesignPattern(name = "Decorator", role = "ConcreteDecorator", description = "Adds eco packaging charge to base price")
public class GiftWrapDecorator extends PriceDecorator {
    private static final double PACKAGING_FEE = 25.0;

    public GiftWrapDecorator(Price wrappee) {
        super(wrappee);
    }

    @Override
    public double getAmount() {
        double total = Math.round((wrappee.getAmount() + PACKAGING_FEE) * 100.0) / 100.0;
        PatternTracer.getInstance().record(
            "Decorator",
            "GiftWrapDecorator",
            "getAmount",
            "Decorated price with Eco-Packaging (+₹25.0). New total: ₹" + total
        );
        return total;
    }

    @Override
    public String getDescription() {
        return wrappee.getDescription() + " + Thermal Eco-Packaging (₹" + PACKAGING_FEE + ")";
    }
}

