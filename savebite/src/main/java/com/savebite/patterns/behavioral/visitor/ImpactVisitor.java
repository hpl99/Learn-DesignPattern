package com.savebite.patterns.behavioral.visitor;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.structural.composite.Bundle;
import com.savebite.patterns.structural.composite.Category;
import com.savebite.patterns.structural.composite.ProductLeaf;
import com.savebite.teaching.PatternTracer;

// Pattern: Visitor
// Category: Behavioral
// Problem it solves here: Concrete Visitor traversing the composite catalog tree to compute platform-wide ecological and economic impact (kg food rescued, rupees saved, and CO2 kg emissions avoided) without mutating catalog node classes.
// GoF participant role: ConcreteVisitor
// Used by: ImpactDashboard, AdminController
@DesignPattern(name = "Visitor", role = "ConcreteVisitor", description = "Calculates kg rescued, rupees saved, and CO2 avoided")
public class ImpactVisitor implements CatalogVisitor {
    private double totalKgRescued = 0.0;
    private double totalRupeesSaved = 0.0;
    private double totalCo2AvoidedKg = 0.0;
    private int visitedNodesCount = 0;

    @Override
    public void visit(ProductLeaf leaf) {
        visitedNodesCount++;
        totalKgRescued += leaf.getWeightKg();
        // Estimated original price vs discounted price savings
        double savings = leaf.getPrice() * 0.5; // ~50% savings
        totalRupeesSaved += savings;
        totalCo2AvoidedKg += (leaf.getWeightKg() * leaf.getCo2FactorPerKg());

        PatternTracer.getInstance().record(
            "Visitor",
            "ImpactVisitor",
            "visit",
            "Visited Leaf: " + leaf.getName() + " (+ " + leaf.getWeightKg() + "kg, +₹" + savings + ", +" + (leaf.getWeightKg() * leaf.getCo2FactorPerKg()) + "kg CO2)"
        );
    }

    @Override
    public void visit(Category category) {
        visitedNodesCount++;
        PatternTracer.getInstance().record(
            "Visitor",
            "ImpactVisitor",
            "visit",
            "Visited Category: " + category.getName()
        );
    }

    @Override
    public void visit(Bundle bundle) {
        visitedNodesCount++;
        // Additional packaging waste reduction credit for bundles
        totalCo2AvoidedKg += 0.5;
        PatternTracer.getInstance().record(
            "Visitor",
            "ImpactVisitor",
            "visit",
            "Visited Bundle: " + bundle.getName() + " (+0.5kg CO2 packaging bonus)"
        );
    }

    public double getTotalKgRescued() {
        return Math.round(totalKgRescued * 100.0) / 100.0;
    }

    public double getTotalRupeesSaved() {
        return Math.round(totalRupeesSaved * 100.0) / 100.0;
    }

    public double getTotalCo2AvoidedKg() {
        return Math.round(totalCo2AvoidedKg * 100.0) / 100.0;
    }

    public int getVisitedNodesCount() {
        return visitedNodesCount;
    }
}

