package com.savebite.service;

import com.savebite.model.Order;
import com.savebite.patterns.creational.prototype.ReorderService;
import com.savebite.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

public class OrderService {
    private final OrderRepository orderRepository;
    private final ReorderService reorderService;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.reorderService = new ReorderService();
    }

    public List<Order> getCustomerOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public Order reorder(String pastOrderId) {
        Order pastOrder = orderRepository.findById(pastOrderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + pastOrderId));

        Order clonedOrder = reorderService.reorder(pastOrder);
        return orderRepository.save(clonedOrder);
    }

    public boolean updateStatus(String orderId, String newStatus) {
        return orderRepository.updateStatus(orderId, newStatus);
    }
}

