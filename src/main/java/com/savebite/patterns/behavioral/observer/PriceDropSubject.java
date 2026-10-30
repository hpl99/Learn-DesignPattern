package com.savebite.patterns.behavioral.observer;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Pattern: Observer
// Category: Behavioral
// Problem it solves here: Concrete subject that fires price reduction notifications whenever a seller or automated scheduler discounts near-expiry food.
// GoF participant role: ConcreteSubject
// Used by: ListingService, Scheduler
@DesignPattern(name = "Observer", role = "ConcreteSubject", description = "Subject broadcasting price drops and state changes")
public class PriceDropSubject implements Subject {
    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    public record PriceDropEvent(String listingId, String title, double oldPrice, double newPrice, String state) {}

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
        PatternTracer.getInstance().record(
            "Observer",
            "PriceDropSubject",
            "attach",
            "Attached observer: " + observer.getObserverName() + " (Total registered: " + observers.size() + ")"
        );
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
        PatternTracer.getInstance().record(
            "Observer",
            "PriceDropSubject",
            "detach",
            "Detached observer: " + observer.getObserverName()
        );
    }

    @Override
    public void notifyObservers(String eventType, Object data) {
        PatternTracer.getInstance().record(
            "Observer",
            "PriceDropSubject",
            "notifyObservers",
            "Broadcasting '" + eventType + "' to " + observers.size() + " registered observers"
        );

        for (Observer obs : observers) {
            try {
                obs.update(eventType, data);
            } catch (Exception e) {
                System.err.println("Observer dispatch error: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Observer> getObservers() {
        return Collections.unmodifiableList(new ArrayList<>(observers));
    }
}

