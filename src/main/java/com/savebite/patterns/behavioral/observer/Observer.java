package com.savebite.patterns.behavioral.observer;

import com.savebite.annotation.DesignPattern;

// Pattern: Observer
// Category: Behavioral
// Problem it solves here: 1-to-many publish-subscribe communication. When listings change price, orders are confirmed, or cart items are modified, multiple loosely-coupled components (cart counter badge, customer price-drop alerts, notification center) must update automatically.
// GoF participant role: Observer
// Used by: CartBadgeObserver, PriceDropAlertObserver, NotificationCenter
@DesignPattern(name = "Observer", role = "Observer", description = "Base Observer interface receiving event notifications")
public interface Observer {
    void update(String eventType, Object data);
    String getObserverName();
}

