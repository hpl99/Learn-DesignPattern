package com.savebite.patterns.behavioral.state;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "State", role = "ConcreteState", description = "Terminal expired state rejecting purchases and discounts")
public class ExpiredState implements ListingState {

    @Override
    public String getStateName() {
        return "EXPIRED";
    }

    @Override
    public boolean canPurchase() {
        return false; // Illegal to purchase expired food
    }

    @Override
    public void applyDiscount(ListingContext context, double percentage) {
        throw new IllegalStateException("Food has expired and been designated for compost/donation. Cannot discount.");
    }

    @Override
    public void tickExpiry(ListingContext context, long minutesRemaining) {
        // Terminal: noop
    }

    @Override
    public void markSold(ListingContext context) {
        throw new IllegalStateException("Cannot sell expired edible goods.");
    }

    @Override
    public void expire(ListingContext context) {
        // Already expired
    }
}

