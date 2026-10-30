package com.savebite.patterns.behavioral.observer;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Pattern: Observer
// Category: Behavioral
// Problem it solves here: Alerts shoppers when favorite or saved bakery items receive a price drop near closing time.
// GoF participant role: ConcreteObserver
// Used by: PriceDropSubject
@DesignPattern(name = "Observer", role = "ConcreteObserver", description = "Alerts interested customers of price drops")
public class PriceDropAlertObserver implements Observer {
    private final List<String> receivedAlerts = new ArrayList<>();

    @Override
    public void update(String eventType, Object data) {
        if ("PRICE_DROP".equalsIgnoreCase(eventType) && data instanceof PriceDropSubject.PriceDropEvent ev) {
            String alert = String.format("PRICE DROP ALERT! '%s' marked down from ₹%.0f to ₹%.0f (%s)",
                ev.title(), ev.oldPrice(), ev.newPrice(), ev.state());
            receivedAlerts.add(alert);

            PatternTracer.getInstance().record(
                "Observer",
                "PriceDropAlertObserver",
                "update",
                "Dispatched customer alert: " + alert
            );
        }
    }

    @Override
    public String getObserverName() {
        return "PriceDropAlertObserver";
    }

    public List<String> getReceivedAlerts() {
        return Collections.unmodifiableList(new ArrayList<>(receivedAlerts));
    }
}

