package com.savebite.patterns.behavioral.visitor;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.structural.composite.Bundle;
import com.savebite.patterns.structural.composite.Category;
import com.savebite.patterns.structural.composite.ProductLeaf;
import com.savebite.teaching.PatternTracer;

// Pattern: Visitor
// Category: Behavioral
// Problem it solves here: Concrete Visitor applying statutory food GST/tax rates across heterogeneous items in the tree.
// GoF participant role: ConcreteVisitor
// Used by: CheckoutFacade
@DesignPattern(name = "Visitor", role = "ConcreteVisitor", description = "Calculates GST tax brackets across catalog tree")
public class TaxVisitor implements CatalogVisitor {
    private double totalTax = 0.0;

    @Override
    public void visit(ProductLeaf leaf) {
        // Essential bakery/dairy items taxed at 5% GST
        double tax = leaf.getPrice() * 0.05;
        totalTax += tax;
        PatternTracer.getInstance().record(
            "Visitor",
            "TaxVisitor",
            "visit",
            "Computed 5% GST tax on " + leaf.getName() + " -> ₹" + Math.round(tax * 100.0) / 100.0
        );
    }

    @Override
    public void visit(Category category) {
        // Noop
    }

    @Override
    public void visit(Bundle bundle) {
        // Noop
    }

    public double getTotalTax() {
        return Math.round(totalTax * 100.0) / 100.0;
    }
}

