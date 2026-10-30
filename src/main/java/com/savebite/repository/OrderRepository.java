package com.savebite.repository;

import com.savebite.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(String id);
    List<Order> findAll();
    List<Order> findByCustomerId(String customerId);
    Order save(Order order);
    boolean updateStatus(String orderId, String newStatus);
}

