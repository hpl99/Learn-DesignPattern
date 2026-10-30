package com.savebite.service;

import com.savebite.model.Listing;
import com.savebite.patterns.behavioral.interpreter.Expression;
import com.savebite.patterns.behavioral.interpreter.Parser;
import com.savebite.patterns.behavioral.iterator.CustomIterator;
import com.savebite.patterns.behavioral.iterator.ListingAggregate;
import com.savebite.patterns.behavioral.iterator.PaginatedListingIterator;
import com.savebite.patterns.behavioral.observer.PriceDropSubject;
import com.savebite.patterns.behavioral.state.ListingContext;
import com.savebite.patterns.behavioral.strategy.DiscountStrategy;
import com.savebite.patterns.behavioral.strategy.ExpiryBasedDiscountStrategy;
import com.savebite.patterns.behavioral.strategy.PricingContext;
import com.savebite.patterns.structural.proxy.CachingListingRepositoryProxy;
import com.savebite.repository.ListingRepository;
import com.savebite.teaching.PatternTracer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListingService {
    private final ListingRepository repository;
    private final PriceDropSubject priceDropSubject;
    private final PricingContext pricingContext;

    public ListingService(ListingRepository repository, PriceDropSubject priceDropSubject) {
        this.repository = repository;
        this.priceDropSubject = priceDropSubject;
        this.pricingContext = new PricingContext(new ExpiryBasedDiscountStrategy());
    }

    public List<Listing> search(String query, String category, String sort, int page, int pageSize) {
        List<Listing> all = repository.findAll();

        // 1. Filter by category if given
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("all")) {
            all = all.stream()
                .filter(l -> l.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        }

        // 2. Evaluate query using Interpreter Expression AST
        if (query != null && !query.isBlank()) {
            Parser parser = new Parser(query);
            Expression expr = parser.parse();
            all = all.stream().filter(expr::interpret).collect(Collectors.toList());
        }

        // 3. Sorting
        if ("price_asc".equalsIgnoreCase(sort)) {
            all.sort(Comparator.comparingDouble(Listing::getCurrentPrice));
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            all.sort(Comparator.comparingDouble(Listing::getCurrentPrice).reversed());
        } else if ("weight".equalsIgnoreCase(sort)) {
            all.sort(Comparator.comparingDouble(Listing::getWeightKg).reversed());
        } else {
            // Default: sort by expiry soonest
            all.sort(Comparator.comparing(Listing::getExpiryTime));
        }

        // 4. Paginate using Iterator pattern
        ListingAggregate aggregate = new ListingAggregate(all);
        CustomIterator<List<Listing>> iterator = aggregate.createPageIterator(pageSize);

        List<Listing> result = new ArrayList<>();
        int curPage = 1;
        while (iterator.hasNext()) {
            List<Listing> chunk = iterator.next();
            if (curPage == page) {
                result = chunk;
                break;
            }
            curPage++;
        }

        return result;
    }

    public int countSearchResults(String query, String category) {
        List<Listing> all = repository.findAll();
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("all")) {
            all = all.stream().filter(l -> l.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
        }
        if (query != null && !query.isBlank()) {
            Parser parser = new Parser(query);
            Expression expr = parser.parse();
            all = all.stream().filter(expr::interpret).collect(Collectors.toList());
        }
        return all.size();
    }

    public Optional<Listing> findById(String id) {
        return repository.findById(id);
    }

    public List<Listing> findBySellerId(String sellerId) {
        return repository.findBySellerId(sellerId);
    }

    public Listing createListing(Listing listing) {
        return repository.save(listing);
    }

    public void updateListingPriceAndState(String listingId, double newPrice, String newState) {
        repository.findById(listingId).ifPresent(listing -> {
            double oldPrice = listing.getCurrentPrice();
            repository.updatePricingAndState(listingId, newPrice, newState);

            if (newPrice < oldPrice) {
                priceDropSubject.notifyObservers(
                    "PRICE_DROP",
                    new PriceDropSubject.PriceDropEvent(listingId, listing.getTitle(), oldPrice, newPrice, newState)
                );
            }
        });
    }

    /**
     * Scheduled job evaluating near-expiry items and advancing State & Strategy discounts
     */
    public void repriceNearExpiryListings() {
        List<Listing> active = repository.findAll().stream()
            .filter(l -> !"SOLD".equalsIgnoreCase(l.getState()) && !"EXPIRED".equalsIgnoreCase(l.getState()))
            .toList();

        for (Listing listing : active) {
            ListingContext context = new ListingContext(listing);
            long minutesLeft = Duration.between(Instant.now(), listing.getExpiryTime()).toMinutes();

            context.tickExpiry(minutesLeft);

            if (context.canPurchase()) {
                double discountedPrice = pricingContext.calculatePrice(listing);
                if (discountedPrice != listing.getCurrentPrice()) {
                    updateListingPriceAndState(listing.getId(), discountedPrice, context.getState().getStateName());
                }
            } else {
                repository.updatePricingAndState(listing.getId(), listing.getCurrentPrice(), context.getState().getStateName());
            }
        }
    }

    public ListingRepository getRepository() {
        return repository;
    }

    public PricingContext getPricingContext() {
        return pricingContext;
    }
}

