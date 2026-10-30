package com.savebite.patterns.structural.composite;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.behavioral.visitor.CatalogVisitor;
import com.savebite.teaching.PatternTracer;

// Pattern: Composite
// Category: Structural
// Problem it solves here: Represents individual food items as leaf elements in the catalog tree.
// GoF participant role: Leaf
// Used by: Category, Bundle, Visitor
@DesignPattern(name = "Composite", role = "Leaf", description = "Leaf element representing a single food listing")
public class ProductLeaf implements CatalogNode {
    private final String id;
    private final String name;
    private final double price;
    private final double weightKg;
    private final double co2FactorPerKg;

    public ProductLeaf(String id, String name, double price, double weightKg) {
        this(id, name, price, weightKg, 2.5); // 2.5 kg CO2 per kg food saved on average
    }

    public ProductLeaf(String id, String name, double price, double weightKg, double co2FactorPerKg) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.weightKg = weightKg;
        this.co2FactorPerKg = co2FactorPerKg;
    }

    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public double getPrice() { return price; }

    @Override
    public double getWeightKg() { return weightKg; }

    public double getCo2FactorPerKg() { return co2FactorPerKg; }

    @Override
    public int getItemCount() { return 1; }

    @Override
    public void accept(CatalogVisitor visitor) {
        PatternTracer.getInstance().record(
            "Composite",
            "ProductLeaf",
            "accept",
            "Visited ProductLeaf: " + name + " (₹" + price + ", " + weightKg + "kg)"
        );
        visitor.visit(this);
    }
}

