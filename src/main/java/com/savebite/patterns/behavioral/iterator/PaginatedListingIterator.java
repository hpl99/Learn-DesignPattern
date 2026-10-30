package com.savebite.patterns.behavioral.iterator;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// Pattern: Iterator
// Category: Behavioral
// Problem it solves here: Iterates over catalog listings chunk by chunk (pages) without loading the entire database table into memory.
// GoF participant role: ConcreteIterator
// Used by: CatalogService, BrowseController
@DesignPattern(name = "Iterator", role = "ConcreteIterator", description = "Paginated chunk iterator for catalog browsing")
public class PaginatedListingIterator implements CustomIterator<List<Listing>> {
    private final List<Listing> listings;
    private final int pageSize;
    private int currentOffset = 0;
    private List<Listing> currentChunk = null;

    public PaginatedListingIterator(List<Listing> listings, int pageSize) {
        this.listings = listings != null ? listings : new ArrayList<>();
        this.pageSize = Math.max(1, pageSize);
    }

    @Override
    public boolean hasNext() {
        return currentOffset < listings.size();
    }

    @Override
    public List<Listing> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more listing pages available.");
        }

        int toIndex = Math.min(currentOffset + pageSize, listings.size());
        this.currentChunk = listings.subList(currentOffset, toIndex);
        int pageNumber = (currentOffset / pageSize) + 1;
        currentOffset = toIndex;

        PatternTracer.getInstance().record(
            "Iterator",
            "PaginatedListingIterator",
            "next",
            "Paging forward to Page " + pageNumber + " (yielding " + currentChunk.size() + " listings)"
        );

        return currentChunk;
    }

    @Override
    public List<Listing> current() {
        return currentChunk;
    }

    public int getCurrentPage() {
        return (currentOffset / pageSize);
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) listings.size() / pageSize);
    }

    public int getTotalItems() {
        return listings.size();
    }
}

