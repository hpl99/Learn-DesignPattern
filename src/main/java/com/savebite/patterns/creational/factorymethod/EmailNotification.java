package com.savebite.patterns.creational.factorymethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Factory Method
// Category: Creational
// Problem it solves here: Concrete email notification delivery for receipts and order confirmations.
// GoF participant role: ConcreteProduct
// Used by: NotificationFactory
@DesignPattern(name = "Factory Method", role = "ConcreteProduct", description = "Simulated SMTP email notification")
public class EmailNotification implements Notification {
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public String send(String recipient, String message) {
        String payload = getFormattedPayload(recipient, message);
        PatternTracer.getInstance().record(
            "Factory Method",
            "EmailNotification",
            "send",
            "Dispatched Email to " + recipient + ": " + message
        );
        return payload;
    }

    @Override
    public String getFormattedPayload(String recipient, String message) {
        return "[EMAIL SMTP] To: " + recipient + " <no-reply@savebite.org> | Body: " + message;
    }
}

