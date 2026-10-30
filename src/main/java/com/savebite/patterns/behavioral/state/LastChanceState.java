package com.savebite.patterns.behavioral.state;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "State", role = "ConcreteState", description = "Last Chance urgent food state (<2 hours remaining)")
public class LastChanceState implements ListingState {

    @Override
    public String getStateName() {
        return "LAST_CHANCE";
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

