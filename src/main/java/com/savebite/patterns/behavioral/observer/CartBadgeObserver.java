package com.savebite.patterns.behavioral.observer;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.concurrent.atomic.AtomicInteger;

// Pattern: Observer
// Category: Behavioral
// Problem it solves here: Concrete observer updating navbar badge numbers whenever cart state changes.
// GoF participant role: ConcreteObserver
// Used by: CartController, Navbar
@DesignPattern(name = "Observer", role = "ConcreteObserver", description = "Updates UI cart counter badge on cart events")
public class CartBadgeObserver implements Observer {
    private final AtomicInteger currentBadgeCount = new AtomicInteger(0);

    @Override
    public void update(String eventType, Object data) {
        if ("CART_UPDATED".equalsIgnoreCase(eventType) && data instanceof Number n) {
            currentBadgeCount.set(n.intValue());
            PatternTracer.getInstance().record(
                "Observer",
                "CartBadgeObserver",
                "update",
                "Cart badge updated to count: " + n.intValue()
            );
        }
    }

    @Override
    public String getObserverName() {
        return "CartBadgeObserver";
    }

    public int getCurrentBadgeCount() {
        return currentBadgeCount.get();
    }
}

