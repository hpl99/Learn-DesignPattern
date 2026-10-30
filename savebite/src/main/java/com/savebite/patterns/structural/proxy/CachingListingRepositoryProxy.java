package com.savebite.patterns.structural.proxy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.repository.ListingRepository;
import com.savebite.teaching.PatternTracer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Pattern: Proxy
// Category: Structural
// Problem it solves here: Listing browsing produces high read volume. Querying H2 repeatedly introduces unnecessary disk I/O. The Caching Proxy intercepts calls to serve hot listings from memory while transparently delegating writes.
// GoF participant role: Proxy
// Used by: ListingService, BrowseController
@DesignPattern(name = "Proxy", role = "Proxy", description = "Caching virtual proxy for ListingRepository")
public class CachingListingRepositoryProxy implements ListingRepository {
    private final ListingRepository realRepository;
    private final Map<String, Listing> itemCache = new ConcurrentHashMap<>();
    private volatile List<Listing> allListingsCache = null;
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

    public CachingListingRepositoryProxy(ListingRepository realRepository) {
        this.realRepository = realRepository;
    }

    @Override
    public Optional<Listing> findById(String id) {
        long start = System.nanoTime();
        if (itemCache.containsKey(id)) {
            cacheHits.incrementAndGet();
            long micros = (System.nanoTime() - start) / 1000;
            PatternTracer.getInstance().record(
                "Proxy",
                "CachingListingRepositoryProxy",
                "findById",
                "Cache HIT for listing " + id + " (took " + micros + " µs, total hits: " + cacheHits.get() + ")"
            );
            return Optional.of(itemCache.get(id));
        }

        cacheMisses.incrementAndGet();
        Optional<Listing> result = realRepository.findById(id);
        long micros = (System.nanoTime() - start) / 1000;
        result.ifPresent(l -> itemCache.put(id, l));

        PatternTracer.getInstance().record(
            "Proxy",
            "CachingListingRepositoryProxy",
            "findById",
            "Cache MISS for listing " + id + " - fetched from DB (took " + micros + " µs, total misses: " + cacheMisses.get() + ")"
        );
        return result;
    }

    @Override
    public List<Listing> findAll() {
        long start = System.nanoTime();
        if (allListingsCache != null) {
            cacheHits.incrementAndGet();
            long micros = (System.nanoTime() - start) / 1000;
            PatternTracer.getInstance().record(
                "Proxy",
                "CachingListingRepositoryProxy",
                "findAll",
                "Cache HIT for findAll (" + allListingsCache.size() + " items, took " + micros + " µs)"
            );
            return allListingsCache;
        }

        cacheMisses.incrementAndGet();
        List<Listing> listings = realRepository.findAll();
        long micros = (System.nanoTime() - start) / 1000;
        this.allListingsCache = Collections.unmodifiableList(listings);
        for (Listing l : listings) {
            itemCache.put(l.getId(), l);
        }

        PatternTracer.getInstance().record(
            "Proxy",
            "CachingListingRepositoryProxy",
            "findAll",
            "Cache MISS for findAll - loaded " + listings.size() + " items from DB (took " + micros + " µs)"
        );
        return listings;
    }

    @Override
    public List<Listing> findBySellerId(String sellerId) {
        return realRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Listing> findByCategory(String category) {
        return realRepository.findByCategory(category);
    }

    @Override
    public synchronized Listing save(Listing listing) {
        Listing saved = realRepository.save(listing);
        itemCache.put(saved.getId(), saved);
        allListingsCache = null; // Invalidate list cache
        PatternTracer.getInstance().record(
            "Proxy",
            "CachingListingRepositoryProxy",
            "save",
            "Pass-through write: saved listing '" + listing.getTitle() + "' and invalidated list cache"
        );
        return saved;
    }

    @Override
    public synchronized boolean updateQuantity(String id, int newQuantity) {
        boolean updated = realRepository.updateQuantity(id, newQuantity);
        if (updated) {
            itemCache.remove(id);
            allListingsCache = null;
        }
        return updated;
    }

    @Override
    public synchronized boolean updatePricingAndState(String id, double newPrice, String newState) {
        boolean updated = realRepository.updatePricingAndState(id, newPrice, newState);
        if (updated) {
            itemCache.remove(id);
            allListingsCache = null;
        }
        return updated;
    }

    @Override
    public synchronized void deleteById(String id) {
        realRepository.deleteById(id);
        itemCache.remove(id);
        allListingsCache = null;
    }

    public synchronized void invalidate() {
        itemCache.clear();
        allListingsCache = null;
    }

    public int getCacheHits() { return cacheHits.get(); }
    public int getCacheMisses() { return cacheMisses.get(); }
    public int getCachedItemCount() { return itemCache.size(); }
}

