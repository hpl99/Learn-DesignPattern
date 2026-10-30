package com.savebite.patterns.behavioral.state;

import com.savebite.annotation.DesignPattern;

// Pattern: State
// Category: Behavioral
// Problem it solves here: A food listing moves through a strict lifecycle (FRESH -> DISCOUNTED -> LAST_CHANCE -> SOLD or EXPIRED). State-specific rules govern whether it can be purchased, how it is priced, and prevents illegal transitions (e.g. attempting to purchase an EXPIRED item or discount a SOLD item).
// GoF participant role: State
// Used by: ListingContext, FreshState, DiscountedState, LastChanceState, SoldState, ExpiredState
@DesignPattern(name = "State", role = "State", description = "Base State interface governing listing lifecycle behavior")
public interface ListingState {
    String getStateName();
    boolean canPurchase();
    void applyDiscount(ListingContext context, double percentage);
    void tickExpiry(ListingContext context, long minutesRemaining);
    void markSold(ListingContext context);
    void expire(ListingContext context);
}

