package com.savebite.patterns.creational.factorymethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Factory Method
// Category: Creational
// Problem it solves here: Concrete in-app toast notification delivery for real-time browser alerts.
// GoF participant role: ConcreteProduct
// Used by: NotificationFactory
@DesignPattern(name = "Factory Method", role = "ConcreteProduct", description = "In-browser UI popup toast notification")
public class ToastNotification implements Notification {
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.TOAST;
    }

    @Override
    public String send(String recipient, String message) {
        String payload = getFormattedPayload(recipient, message);
        PatternTracer.getInstance().record(
            "Factory Method",
            "ToastNotification",
            "send",
            "Dispatched Toast notification to " + recipient + ": " + message
        );
        return payload;
    }

    @Override
    public String getFormattedPayload(String recipient, String message) {
        return "[TOAST UI] To: " + recipient + " | Message: " + message;
    }
}

