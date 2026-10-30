package com.savebite.patterns.behavioral.state;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

// Pattern: State
// Category: Behavioral
// Problem it solves here: The Context maintaining the current ListingState reference and exposing business methods that delegate to the active state.
// GoF participant role: Context
// Used by: ListingService, Scheduler
@DesignPattern(name = "State", role = "Context", description = "Context maintaining active state of a Listing")
public class ListingContext {
    private final Listing listing;
    private ListingState state;

    public ListingContext(Listing listing) {
        this.listing = listing;
        this.state = parseState(listing.getState());
    }

    private ListingState parseState(String stateName) {
        if (stateName == null) return new FreshState();
        return switch (stateName.toUpperCase()) {
            case "DISCOUNTED" -> new DiscountedState();
            case "LAST_CHANCE" -> new LastChanceState();
            case "SOLD" -> new SoldState();
            case "EXPIRED" -> new ExpiredState();
            default -> new FreshState();
        };
    }

    public void setState(ListingState newState) {
        String oldState = this.state.getStateName();
        this.state = newState;
        this.listing.setState(newState.getStateName());

        PatternTracer.getInstance().record(
            "State",
            "ListingContext",
            "setState",
            "Listing [" + listing.getTitle() + "] transitioned: " + oldState + " -> " + newState.getStateName()
        );
    }

    public boolean canPurchase() {
        return state.canPurchase();
    }

    public void applyDiscount(double percentage) {
        state.applyDiscount(this, percentage);
    }

    public void tickExpiry(long minutesRemaining) {
        state.tickExpiry(this, minutesRemaining);
    }

    public void markSold() {
        state.markSold(this);
    }

    public void expire() {
        state.expire(this);
    }

    public Listing getListing() { return listing; }
    public ListingState getState() { return state; }
}

