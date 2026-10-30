package com.savebite.controller;

import com.savebite.model.Listing;
import com.savebite.patterns.creational.builder.ListingBuilder;
import com.savebite.patterns.creational.prototype.ListingPrototype;
import com.savebite.service.ListingService;
import io.javalin.http.Context;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ListingController {
    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    public void searchListings(Context ctx) {
        String q = ctx.queryParam("q");
        String category = ctx.queryParam("category");
        String sort = ctx.queryParam("sort");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int pageSize = ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(9);

        List<Listing> listings = listingService.search(q, category, sort, page, pageSize);
        int total = listingService.countSearchResults(q, category);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        ctx.json(Map.of(
            "listings", listings,
            "page", page,
            "pageSize", pageSize,
            "total", total,
            "totalPages", totalPages
        ));
    }

    public void getListingById(Context ctx) {
        String id = ctx.pathParam("id");
        Optional<Listing> listing = listingService.findById(id);
        if (listing.isPresent()) {
            ctx.json(listing.get());
        } else {
            ctx.status(404).json(Map.of("error", "Listing not found: " + id));
        }
    }

    public void createListing(Context ctx) {
        Map<String, Object> body = ctx.bodyAsClass(Map.class);
        String sellerId = ctx.attribute("userId");
        String sellerName = ctx.attribute("userEmail");

        Listing listing = new ListingBuilder()
            .withSeller(sellerId != null ? sellerId : "seller-demo", sellerName != null ? sellerName : "Artisan Bakery")
            .withTitle((String) body.get("title"))
            .withDescription((String) body.get("description"))
            .withCategory((String) body.getOrDefault("category", "bakery"))
            .withPricing(
                Double.parseDouble(String.valueOf(body.get("originalPrice"))),
                Double.parseDouble(String.valueOf(body.getOrDefault("currentPrice", body.get("originalPrice"))))
            )
            .withQuantity(Integer.parseInt(String.valueOf(body.getOrDefault("quantity", 1))))
            .withWeightKg(Double.parseDouble(String.valueOf(body.getOrDefault("weightKg", 0.5))))
            .withPhotoUrl((String) body.getOrDefault("photoUrl", "/img/placeholder.svg"))
            .build();

        Listing saved = listingService.createListing(listing);
        ctx.status(201).json(saved);
    }

    public void cloneListingTemplate(Context ctx) {
        String id = ctx.pathParam("id");
        Optional<Listing> original = listingService.findById(id);
        if (original.isEmpty()) {
            ctx.status(404).json(Map.of("error", "Template listing not found: " + id));
            return;
        }

        ListingPrototype prototype = new ListingPrototype(original.get());
        Listing cloned = prototype.copy();
        Listing saved = listingService.createListing(cloned);
        ctx.status(201).json(saved);
    }
}

