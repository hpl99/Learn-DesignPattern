package com.savebite.patterns.structural.decorator;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Decorator
// Category: Structural
// Problem it solves here: Adds a priority slot reservation fee (+ ₹15) for guaranteed front-of-line collection during peak rush hours.
// GoF participant role: ConcreteDecorator
// Used by: CheckoutFacade
@DesignPattern(name = "Decorator", role = "ConcreteDecorator", description = "Adds express priority slot reservation fee")
public class PickupPriorityDecorator extends PriceDecorator {
    private static final double PRIORITY_FEE = 15.0;

    public PickupPriorityDecorator(Price wrappee) {
        super(wrappee);
    }

    @Override
    public double getAmount() {
        double total = Math.round((wrappee.getAmount() + PRIORITY_FEE) * 100.0) / 100.0;
        PatternTracer.getInstance().record(
            "Decorator",
            "PickupPriorityDecorator",
            "getAmount",
            "Decorated price with Priority Pickup Slot (+₹15.0). New total: ₹" + total
        );
        return total;
    }

    @Override
    public String getDescription() {
        return wrappee.getDescription() + " + Priority Rush Slot (₹" + PRIORITY_FEE + ")";
    }
}

