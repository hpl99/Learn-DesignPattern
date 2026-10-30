package com.savebite.patterns.behavioral.iterator;

import com.savebite.annotation.DesignPattern;

// Pattern: Iterator
// Category: Behavioral
// Problem it solves here: Accesses items in a surplus catalog sequentially without exposing underlying collection data structures (ArrayList, DB cursor, or array buffer).
// GoF participant role: Iterator
// Used by: PaginatedListingIterator, CarouselIterator
@DesignPattern(name = "Iterator", role = "Iterator", description = "Base iterator interface for sequential traversal")
public interface CustomIterator<T> {
    boolean hasNext();
    T next();
    T current();
}

