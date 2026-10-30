package com.savebite.patterns.structural.flyweight;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Pattern: Flyweight
// Category: Structural
// Problem it solves here: Manages the pool of immutable shared badge instances so hundreds of items reuse the exact same memory references.
// GoF participant role: FlyweightFactory
// Used by: ListingService, CatalogController
@DesignPattern(name = "Flyweight", role = "FlyweightFactory", description = "Factory pooling and caching shared Badge flyweights")
public class BadgeFactory {
    private static final Map<String, BadgeFlyweight> BADGE_POOL = new ConcurrentHashMap<>();
    private static final AtomicInteger LOOKUP_COUNT = new AtomicInteger(0);

    static {
        // Pre-populate canonical badges
        register("FRESH", new BadgeFlyweight("FRESH", "Fresh Arrival", "🌱", "#dcfce7", "#166534", "Listed within the last 2 hours"));
        register("DISCOUNTED", new BadgeFlyweight("DISCOUNTED", "50% OFF", "🏷️", "#fef3c7", "#92400e", "Discounted near closing time"));
        register("LAST_CHANCE", new BadgeFlyweight("LAST_CHANCE", "Last Chance (<2h)", "⚡", "#fee2e2", "#991b1b", "Expiring very soon, maximum discount!"));
        register("ORGANIC", new BadgeFlyweight("ORGANIC", "100% Organic", "🌿", "#e0f2fe", "#075985", "Certified organic ingredients"));
        register("VEGAN", new BadgeFlyweight("VEGAN", "Plant Based", "🥑", "#f3e8ff", "#6b21a8", "100% vegan certified"));
        register("RESCUE_HERO", new BadgeFlyweight("RESCUE_HERO", "Zero Waste Hero", "🦸", "#ecfdf5", "#065f46", "High impact food rescue"));
    }

    public static void register(String key, BadgeFlyweight badge) {
        BADGE_POOL.put(key.toUpperCase(), badge);
    }

    public static BadgeFlyweight getBadge(String key) {
        LOOKUP_COUNT.incrementAndGet();
        String upper = key.toUpperCase();
        BadgeFlyweight badge = BADGE_POOL.computeIfAbsent(upper, k ->
            new BadgeFlyweight(k, k, "✨", "#f1f5f9", "#334155", "Special item badge")
        );

        PatternTracer.getInstance().record(
            "Flyweight",
            "BadgeFactory",
            "getBadge",
            "Shared badge lookup for '" + key + "'. Total pooled instances: " + BADGE_POOL.size() + ", Total lookups: " + LOOKUP_COUNT.get()
        );

        return badge;
    }

    public static Map<String, BadgeFlyweight> getPool() {
        return Collections.unmodifiableMap(BADGE_POOL);
    }

    public static int getPoolSize() {
        return BADGE_POOL.size();
    }

    public static int getLookupCount() {
        return LOOKUP_COUNT.get();
    }
}

