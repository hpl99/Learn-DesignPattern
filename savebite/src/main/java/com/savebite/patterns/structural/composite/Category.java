package com.savebite.patterns.structural.composite;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.behavioral.visitor.CatalogVisitor;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Pattern: Composite
// Category: Structural
// Problem it solves here: Represents hierarchical product groupings (e.g., "Bakery", "Dairy", "Prepared Meals") containing subcategories and items.
// GoF participant role: Composite
// Used by: CatalogService, CatalogVisitor
@DesignPattern(name = "Composite", role = "Composite", description = "Composite node representing a catalog category")
public class Category implements CatalogNode {
    private final String name;
    private final List<CatalogNode> children = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    public void add(CatalogNode node) {
        children.add(node);
        PatternTracer.getInstance().record(
            "Composite",
            "Category",
            "add",
            "Added '" + node.getName() + "' to category '" + name + "'"
        );
    }

    public void remove(CatalogNode node) {
        children.remove(node);
    }

    public List<CatalogNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        double sum = 0.0;
        for (CatalogNode child : children) {
            sum += child.getPrice();
        }
        return sum;
    }

    @Override
    public double getWeightKg() {
        double totalWeight = 0.0;
        for (CatalogNode child : children) {
            totalWeight += child.getWeightKg();
        }
        return totalWeight;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (CatalogNode child : children) {
            count += child.getItemCount();
        }
        return count;
    }

    @Override
    public void accept(CatalogVisitor visitor) {
        PatternTracer.getInstance().record(
            "Composite",
            "Category",
            "accept",
            "Visiting Category '" + name + "' with " + children.size() + " children"
        );
        visitor.visit(this);
        for (CatalogNode child : children) {
            child.accept(visitor);
        }
    }
}

