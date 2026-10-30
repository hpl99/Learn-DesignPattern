package com.savebite.patterns.structural.composite;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.behavioral.visitor.CatalogVisitor;

// Pattern: Composite
// Category: Structural
// Problem it solves here: Treats individual food listings, multi-item Rescue Box bundles, and whole bakery categories uniformly in catalog searches, pricing, and impact calculations.
// GoF participant role: Component
// Used by: Category, Bundle, ProductLeaf, CatalogVisitor
@DesignPattern(name = "Composite", role = "Component", description = "Base component interface for hierarchical catalog tree")
public interface CatalogNode {
    String getName();
    double getPrice();
    double getWeightKg();
    int getItemCount();
    void accept(CatalogVisitor visitor);
}

