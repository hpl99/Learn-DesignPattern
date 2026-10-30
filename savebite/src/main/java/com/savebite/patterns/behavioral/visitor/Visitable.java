package com.savebite.patterns.behavioral.visitor;

import com.savebite.annotation.DesignPattern;

// Pattern: Visitor
// Category: Behavioral
// Problem it solves here: Common interface for elements capable of accepting a CatalogVisitor.
// GoF participant role: Element
// Used by: CatalogNode, ProductLeaf, Category, Bundle
@DesignPattern(name = "Visitor", role = "Element", description = "Element interface accepting a CatalogVisitor")
public interface Visitable {
    void accept(CatalogVisitor visitor);
}

