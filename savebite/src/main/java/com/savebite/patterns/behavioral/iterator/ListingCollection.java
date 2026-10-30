package com.savebite.patterns.behavioral.iterator;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;

import java.util.List;

// Pattern: Iterator
// Category: Behavioral
// Problem it solves here: Aggregate interface defining factory methods for obtaining specialized iterators (paginated, circular hero carousel).
// GoF participant role: Aggregate
// Used by: ListingAggregate
@DesignPattern(name = "Iterator", role = "Aggregate", description = "Aggregate interface creating catalog iterators")
public interface ListingCollection {
    CustomIterator<List<Listing>> createPageIterator(int pageSize);
    CarouselIterator createCarouselIterator();
    void add(Listing listing);
    int size();
}

