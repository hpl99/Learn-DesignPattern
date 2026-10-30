package com.savebite.patterns.structural.decorator;

import com.savebite.annotation.DesignPattern;

// Pattern: Decorator
// Category: Structural
// Problem it solves here: Base decorator holding a wrapped Price reference, delegating basic calls to it.
// GoF participant role: Decorator
// Used by: GiftWrapDecorator, CouponDecorator, PickupPriorityDecorator
@DesignPattern(name = "Decorator", role = "Decorator", description = "Base decorator maintaining wrappee Price reference")
public abstract class PriceDecorator implements Price {
    protected final Price wrappee;

    protected PriceDecorator(Price wrappee) {
        if (wrappee == null) {
            throw new IllegalArgumentException("Wrappee price cannot be null");
        }
        this.wrappee = wrappee;
    }

    @Override
    public double getAmount() {
        return wrappee.getAmount();
    }

    @Override
    public String getDescription() {
        return wrappee.getDescription();
    }
}

