package com.savebite.patterns.creational.factorymethod;

import com.savebite.annotation.DesignPattern;

// Pattern: Factory Method
// Category: Creational
// Problem it solves here: Defines a common interface for notifications (Toast, Email, SMS) so clients can trigger alerts without coupling to delivery-specific implementation details.
// GoF participant role: Product
// Used by: NotificationFactory, NotificationCenter, OrderService
@DesignPattern(name = "Factory Method", role = "Product", description = "Abstract product interface for notifications")
public interface Notification {
    NotificationChannel getChannel();
    String send(String recipient, String message);
    String getFormattedPayload(String recipient, String message);
}

