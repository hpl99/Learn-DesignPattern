package com.savebite.patterns.behavioral.iterator;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;

import java.util.ArrayList;
import java.util.List;

// Pattern: Iterator
// Category: Behavioral
// Problem it solves here: Concrete aggregate holding the underlying listing collection and producing specialized iterators on demand.
// GoF participant role: ConcreteAggregate
// Used by: CatalogService
@DesignPattern(name = "Iterator", role = "ConcreteAggregate", description = "Concrete listing aggregate producing iterators")
public class ListingAggregate implements ListingCollection {
    private final List<Listing> listings = new ArrayList<>();

    public ListingAggregate() {}

    public ListingAggregate(List<Listing> listings) {
        if (listings != null) {
            this.listings.addAll(listings);
        }
    }

    @Override
    public CustomIterator<List<Listing>> createPageIterator(int pageSize) {
        return new PaginatedListingIterator(listings, pageSize);
    }

    @Override
    public CarouselIterator createCarouselIterator() {
        return new CarouselIterator(listings);
    }

    @Override
    public void add(Listing listing) {
        listings.add(listing);
    }

    @Override
    public int size() {
        return listings.size();
    }
}

