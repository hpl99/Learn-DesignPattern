package com.savebite.patterns.creational.factorymethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Factory Method
// Category: Creational
// Problem it solves here: Concrete SMS gateway notification for urgent near-expiry pickup alerts.
// GoF participant role: ConcreteProduct
// Used by: NotificationFactory
@DesignPattern(name = "Factory Method", role = "ConcreteProduct", description = "Simulated SMS cellular alert")
public class SmsNotification implements Notification {
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public String send(String recipient, String message) {
        String payload = getFormattedPayload(recipient, message);
        PatternTracer.getInstance().record(
            "Factory Method",
            "SmsNotification",
            "send",
            "Dispatched SMS to phone " + recipient + ": " + message
        );
        return payload;
    }

    @Override
    public String getFormattedPayload(String recipient, String message) {
        return "[SMS GATEWAY] To: " + recipient + " | Text: " + message;
    }
}

