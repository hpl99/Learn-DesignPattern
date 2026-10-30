package com.savebite.patterns.behavioral.state;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "State", role = "ConcreteState", description = "Terminal sold-out state rejecting purchases")
public class SoldState implements ListingState {

    @Override
    public String getStateName() {
        return "SOLD";
    }

    @Override
    public boolean canPurchase() {
        return false; // Illegal to purchase sold listing
    }

    @Override
    public void applyDiscount(ListingContext context, double percentage) {
        throw new IllegalStateException("Cannot apply discount to a SOLD listing.");
    }

    @Override
    public void tickExpiry(ListingContext context, long minutesRemaining) {
        // Terminal state: noop
    }

    @Override
    public void markSold(ListingContext context) {
        // Already sold
    }

    @Override
    public void expire(ListingContext context) {
        throw new IllegalStateException("Cannot expire a listing that is already SOLD.");
    }
}

