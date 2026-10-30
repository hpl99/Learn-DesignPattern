package com.savebite.patterns.behavioral.observer;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Pattern: Observer
// Category: Behavioral
// Problem it solves here: Aggregates all in-app notifications (price drops, order confirmations, reminders) in a single notification tray for the user.
// GoF participant role: ConcreteObserver
// Used by: NotificationCenterController
@DesignPattern(name = "Observer", role = "ConcreteObserver", description = "In-app notification center collecting all alerts")
public class NotificationCenter implements Observer {
    public record NotificationItem(String id, String title, String body, Instant timestamp, boolean read) {}
    private final List<NotificationItem> items = new ArrayList<>();

    @Override
    public synchronized void update(String eventType, Object data) {
        String title = "System Notification: " + eventType;
        String body = data != null ? data.toString() : "Event triggered";

        if (data instanceof PriceDropSubject.PriceDropEvent p) {
            title = "🔥 Flash Price Drop: " + p.title();
            body = "Price reduced to ₹" + p.newPrice() + " (" + p.state() + ")";
        }

        NotificationItem item = new NotificationItem(UUID.randomUUID().toString(), title, body, Instant.now(), false);
        items.add(0, item);

        PatternTracer.getInstance().record(
            "Observer",
            "NotificationCenter",
            "update",
            "Notification Center received '" + title + "'"
        );
    }

    @Override
    public String getObserverName() {
        return "NotificationCenter";
    }

    public synchronized List<NotificationItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public synchronized int getUnreadCount() {
        return (int) items.stream().filter(i -> !i.read()).count();
    }
}

