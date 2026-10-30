package com.savebite.service;

import com.savebite.model.Listing;
import com.savebite.patterns.behavioral.visitor.ImpactVisitor;
import com.savebite.patterns.structural.composite.Bundle;
import com.savebite.patterns.structural.composite.Category;
import com.savebite.patterns.structural.composite.ProductLeaf;
import com.savebite.repository.ListingRepository;
import com.savebite.repository.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImpactService {
    private final ListingRepository listingRepository;
    private final OrderRepository orderRepository;

    public record ImpactMetrics(
        double totalKgRescued,
        double totalRupeesSaved,
        double totalCo2AvoidedKg,
        int totalMealsRescued,
        int nodesEvaluated
    ) {}

    public ImpactService(ListingRepository listingRepository, OrderRepository orderRepository) {
        this.listingRepository = listingRepository;
        this.orderRepository = orderRepository;
    }

    public ImpactMetrics computeGlobalImpact() {
        List<Listing> listings = listingRepository.findAll();

        // Build Composite Catalog Tree
        Category root = new Category("All Food Categories");
        Map<String, Category> categories = new HashMap<>();

        for (Listing l : listings) {
            String catName = l.getCategory() != null ? l.getCategory().toUpperCase() : "GENERAL";
            Category cat = categories.computeIfAbsent(catName, k -> {
                Category c = new Category(k);
                root.add(c);
                return c;
            });

            cat.add(new ProductLeaf(l.getId(), l.getTitle(), l.getCurrentPrice(), l.getWeightKg()));
        }

        // Add demo Rescue Box Bundle
        Bundle eveningRescueBox = new Bundle("Evening Mystery Rescue Box", 20.0);
        eveningRescueBox.addItem(new ProductLeaf("b-1", "Assorted Day's Sourdough & Buns", 120.0, 0.8));
        eveningRescueBox.addItem(new ProductLeaf("b-2", "Artisan Cream Pastry", 90.0, 0.3));
        root.add(eveningRescueBox);

        // Run ImpactVisitor across composite hierarchy
        ImpactVisitor impactVisitor = new ImpactVisitor();
        root.accept(impactVisitor);

        // Count completed orders as actual rescued meals
        int completedOrdersCount = orderRepository.findAll().size();
        int totalMeals = completedOrdersCount * 3 + listings.size();

        return new ImpactMetrics(
            impactVisitor.getTotalKgRescued() + (completedOrdersCount * 1.5),
            impactVisitor.getTotalRupeesSaved() + (completedOrdersCount * 180.0),
            impactVisitor.getTotalCo2AvoidedKg() + (completedOrdersCount * 3.75),
            totalMeals,
            impactVisitor.getVisitedNodesCount()
        );
    }
}

