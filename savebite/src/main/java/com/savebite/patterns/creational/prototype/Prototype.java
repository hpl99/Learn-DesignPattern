package com.savebite.patterns.creational.prototype;

import com.savebite.annotation.DesignPattern;

// Pattern: Prototype
// Category: Creational
// Problem it solves here: Allows rapid creation of new objects by cloning pre-configured prototypes (e.g., past orders or listing templates) instead of assembling from scratch.
// GoF participant role: Prototype
// Used by: ReorderService, SellerListingCloner
@DesignPattern(name = "Prototype", role = "Prototype", description = "Interface for deep copyable prototypes")
public interface Prototype<T> {
    T copy();
}

