package com.savebite.patterns.creational.prototype;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

import java.time.Instant;
import java.util.UUID;

// Pattern: Prototype
// Category: Creational
// Problem it solves here: Sellers frequently list the same daily surplus items (e.g. "Evening Croissants"). Cloning an existing template preserves photos, category, and weight while assigning a new ID and expiry time.
// GoF participant role: ConcretePrototype
// Used by: SellerController, ReorderService
@DesignPattern(name = "Prototype", role = "ConcretePrototype", description = "Clonable listing template with deep copy")
public class ListingPrototype implements Prototype<Listing> {
    private final Listing template;

    public ListingPrototype(Listing template) {
        this.template = template;
    }

    @Override
    public Listing copy() {
        String newId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        Instant newExpiry = now.plusSeconds(3600 * 4); // fresh 4h window

        Listing clone = new Listing(
            newId,
            template.getSellerId(),
            template.getSellerName(),
            template.getTitle() + " (Batch " + now.toEpochMilli() % 1000 + ")",
            template.getDescription(),
            template.getCategory(),
            template.getOriginalPrice(),
            template.getCurrentPrice(),
            template.getQuantity(),
            newExpiry,
            "FRESH",
            template.getPhotoUrl(),
            template.getWeightKg(),
            now
        );

        PatternTracer.getInstance().record(
            "Prototype",
            "ListingPrototype",
            "copy",
            "Deep cloned Listing template [Orig ID: " + template.getId() + "] -> [Clone ID: " + clone.getId() + "]"
        );

        return clone;
    }

    public Listing getTemplate() {
        return template;
    }
}

