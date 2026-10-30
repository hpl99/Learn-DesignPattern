package com.savebite.patterns.behavioral.iterator;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// Pattern: Iterator
// Category: Behavioral
// Problem it solves here: Circular bidirectional carousel navigation across featured hero listings, wrapping cleanly around start/end boundaries.
// GoF participant role: ConcreteIterator
// Used by: HeroBannerController
@DesignPattern(name = "Iterator", role = "ConcreteIterator", description = "Circular bidirectional carousel iterator")
public class CarouselIterator implements CustomIterator<Listing> {
    private final List<Listing> items;
    private int cursor = -1;

    public CarouselIterator(List<Listing> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    @Override
    public boolean hasNext() {
        return !items.isEmpty();
    }

    @Override
    public Listing next() {
        if (items.isEmpty()) throw new NoSuchElementException("Carousel is empty");
        cursor = (cursor + 1) % items.size();
        Listing item = items.get(cursor);

        PatternTracer.getInstance().record(
            "Iterator",
            "CarouselIterator",
            "next",
            "Carousel stepped forward -> Index " + cursor + ": '" + item.getTitle() + "'"
        );

        return item;
    }

    public Listing previous() {
        if (items.isEmpty()) throw new NoSuchElementException("Carousel is empty");
        cursor = (cursor - 1 + items.size()) % items.size();
        Listing item = items.get(cursor);

        PatternTracer.getInstance().record(
            "Iterator",
            "CarouselIterator",
            "previous",
            "Carousel stepped backward -> Index " + cursor + ": '" + item.getTitle() + "'"
        );

        return item;
    }

    @Override
    public Listing current() {
        if (items.isEmpty() || cursor < 0) return null;
        return items.get(cursor);
    }

    public int getCursor() {
        return cursor;
    }
}
