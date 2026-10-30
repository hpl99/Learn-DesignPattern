package com.savebite.patterns.behavioral.state;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "State", role = "ConcreteState", description = "Discounted near-expiry food state")
public class DiscountedState implements ListingState {

    @Override
    public String getStateName() {
        return "DISCOUNTED";
    }

    @Override
    public boolean canPurchase() {
        return true;
    }

    @Override
    public void applyDiscount(ListingContext context, double percentage) {
        double newPrice = context.getListing().getOriginalPrice() * (1.0 - (percentage / 100.0));
        context.getListing().setCurrentPrice(Math.round(newPrice * 100.0) / 100.0);
    }

    @Override
    public void tickExpiry(ListingContext context, long minutesRemaining) {
        if (minutesRemaining <= 0) {
            context.setState(new ExpiredState());
        } else if (minutesRemaining <= 120) {
            context.setState(new LastChanceState());
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

