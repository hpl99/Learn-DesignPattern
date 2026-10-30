package com.savebite.patterns.creational.factorymethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Factory Method
// Category: Creational
// Problem it solves here: Encapsulates instantiation logic of polymorphic Notification objects based on user preferences and alert channels.
// GoF participant role: Creator
// Used by: NotificationCenter, Observer listeners
@DesignPattern(name = "Factory Method", role = "Creator", description = "Factory class creating Notification instances")
public class NotificationFactory {

    public Notification createNotification(NotificationChannel channel) {
        Notification notification = switch (channel) {
            case TOAST -> new ToastNotification();
            case EMAIL -> new EmailNotification();
            case SMS -> new SmsNotification();
        };

        PatternTracer.getInstance().record(
            "Factory Method",
            "NotificationFactory",
            "createNotification",
            "Created " + notification.getClass().getSimpleName() + " for channel " + channel
        );

        return notification;
    }
}

