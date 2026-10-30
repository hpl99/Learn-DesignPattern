package com.savebite.patterns.behavioral.state;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: State
// Category: Behavioral
// Problem it solves here: Concrete State representing food listed freshly with standard initial price.
// GoF participant role: ConcreteState
// Used by: ListingContext
@DesignPattern(name = "State", role = "ConcreteState", description = "Freshly listed surplus food state")
public class FreshState implements ListingState {

    @Override
    public String getStateName() {
        return "FRESH";
    }

    @Override
    public boolean canPurchase() {
        return true;
    }

    @Override
    public void applyDiscount(ListingContext context, double percentage) {
        double newPrice = context.getListing().getOriginalPrice() * (1.0 - (percentage / 100.0));
        context.getListing().setCurrentPrice(Math.round(newPrice * 100.0) / 100.0);
        context.setState(new DiscountedState());
    }

    @Override
    public void tickExpiry(ListingContext context, long minutesRemaining) {
        if (minutesRemaining <= 0) {
            context.setState(new ExpiredState());
        } else if (minutesRemaining <= 120) { // < 2 hours
            context.setState(new LastChanceState());
        } else if (minutesRemaining <= 240) { // < 4 hours
            context.setState(new DiscountedState());
        }
    }

    @Override
    public void markSold(ListingContext context) {
        context.setState(new SoldState());
    }

    @Override
    public void expire(ListingContext context) {
        context.setState(new ExpiredState());
    }
}

