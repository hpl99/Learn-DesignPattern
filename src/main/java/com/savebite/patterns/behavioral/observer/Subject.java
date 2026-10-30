package com.savebite.patterns.behavioral.observer;

import com.savebite.annotation.DesignPattern;

import java.util.List;

// Pattern: Observer
// Category: Behavioral
// Problem it solves here: Subject interface managing observer registrations and event broadcasts.
// GoF participant role: Subject
// Used by: PriceDropSubject, NotificationCenter
@DesignPattern(name = "Observer", role = "Subject", description = "Base Subject interface managing observer list and dispatch")
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String eventType, Object data);
    List<Observer> getObservers();
}

