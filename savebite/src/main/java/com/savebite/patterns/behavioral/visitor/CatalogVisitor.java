package com.savebite.patterns.behavioral.visitor;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.structural.composite.Bundle;
import com.savebite.patterns.structural.composite.Category;
import com.savebite.patterns.structural.composite.ProductLeaf;

// Pattern: Visitor
// Category: Behavioral
// Problem it solves here: Allows new operations (calculating total ecological impact, weight, or taxes) to be performed over heterogeneous catalog composite nodes without altering their class definitions.
// GoF participant role: Visitor
// Used by: ImpactVisitor, WeightVisitor, TaxVisitor
@DesignPattern(name = "Visitor", role = "Visitor", description = "Visitor interface for traversing catalog composite tree")
public interface CatalogVisitor {
    void visit(ProductLeaf leaf);
    void visit(Category category);
    void visit(Bundle bundle);
}

