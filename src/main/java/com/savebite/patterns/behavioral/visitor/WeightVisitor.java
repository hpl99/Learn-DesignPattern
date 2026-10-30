package com.savebite.patterns.behavioral.visitor;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.structural.composite.Bundle;
import com.savebite.patterns.structural.composite.Category;
import com.savebite.patterns.structural.composite.ProductLeaf;
import com.savebite.teaching.PatternTracer;

// Pattern: Visitor
// Category: Behavioral
// Problem it solves here: Concrete Visitor computing aggregate physical payload weight across complex composite catalog branches or rescue box bundles.
// GoF participant role: ConcreteVisitor
// Used by: LogisticsService
@DesignPattern(name = "Visitor", role = "ConcreteVisitor", description = "Calculates total weight of composite catalog branches")
public class WeightVisitor implements CatalogVisitor {
    private double totalWeightKg = 0.0;

    @Override
    public void visit(ProductLeaf leaf) {
        totalWeightKg += leaf.getWeightKg();
        PatternTracer.getInstance().record(
            "Visitor",
            "WeightVisitor",
            "visit",
            "Accumulated weight for " + leaf.getName() + " (" + leaf.getWeightKg() + "kg)"
        );
    }

    @Override
    public void visit(Category category) {
        // Categories don't carry intrinsic weight
    }

    @Override
    public void visit(Bundle bundle) {
        // Bundles accumulate items in recursion
    }

    public double getTotalWeightKg() {
        return Math.round(totalWeightKg * 100.0) / 100.0;
    }
}

