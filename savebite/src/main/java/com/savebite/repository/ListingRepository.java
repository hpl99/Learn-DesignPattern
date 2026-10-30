package com.savebite.repository;

import com.savebite.model.Listing;

import java.util.List;
import java.util.Optional;

public interface ListingRepository {
    Optional<Listing> findById(String id);
    List<Listing> findAll();
    List<Listing> findBySellerId(String sellerId);
    List<Listing> findByCategory(String category);
    Listing save(Listing listing);
    boolean updateQuantity(String id, int newQuantity);
    boolean updatePricingAndState(String id, double newPrice, String newState);
    void deleteById(String id);
}

