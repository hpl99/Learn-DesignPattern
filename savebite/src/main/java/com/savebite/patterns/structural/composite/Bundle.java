package com.savebite.patterns.structural.composite;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.behavioral.visitor.CatalogVisitor;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Pattern: Composite
// Category: Structural
// Problem it solves here: Represents a mystery "Rescue Box" containing multiple food items sold together as a single package with a bundle discount.
// GoF participant role: Composite
// Used by: CatalogService, CatalogVisitor
@DesignPattern(name = "Composite", role = "Composite", description = "Rescue Box multi-product bundle composite")
public class Bundle implements CatalogNode {
    private final String name;
    private final double bundleDiscountPercentage;
    private final List<CatalogNode> items = new ArrayList<>();

    public Bundle(String name, double bundleDiscountPercentage) {
        this.name = name;
        this.bundleDiscountPercentage = bundleDiscountPercentage;
    }

    public void addItem(CatalogNode node) {
        items.add(node);
        PatternTracer.getInstance().record(
            "Composite",
            "Bundle",
            "addItem",
            "Added '" + node.getName() + "' to Rescue Box Bundle '" + name + "'"
        );
    }

    public List<CatalogNode> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public String getName() {
        return name + " [Rescue Box - " + (int)bundleDiscountPercentage + "% OFF]";
    }

    @Override
    public double getPrice() {
        double rawTotal = 0.0;
        for (CatalogNode item : items) {
            rawTotal += item.getPrice();
        }
        return Math.round(rawTotal * (1.0 - (bundleDiscountPercentage / 100.0)) * 100.0) / 100.0;
    }

    @Override
    public double getWeightKg() {
        double totalWeight = 0.0;
        for (CatalogNode item : items) {
            totalWeight += item.getWeightKg();
        }
        return Math.round(totalWeight * 100.0) / 100.0;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (CatalogNode item : items) {
            count += item.getItemCount();
        }
        return count;
    }

    @Override
    public void accept(CatalogVisitor visitor) {
        PatternTracer.getInstance().record(
            "Composite",
            "Bundle",
            "accept",
            "Visiting Rescue Box Bundle: " + name + " (" + items.size() + " items, discounted total ₹" + getPrice() + ")"
        );
        visitor.visit(this);
        for (CatalogNode item : items) {
            item.accept(visitor);
        }
    }
}

