package com.savebite.patterns.creational.builder;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Order;
import com.savebite.model.OrderItem;
import com.savebite.teaching.PatternTracer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Pattern: Builder
// Category: Creational
// Problem it solves here: Assembles multi-item customer orders with pickup slot selection, address validation, total computation, and payment choices.
// GoF participant role: Builder
// Used by: CheckoutFacade, OrderService
@DesignPattern(name = "Builder", role = "Builder", description = "Fluent builder constructing orders step-by-step")
public class OrderBuilder {
    private String id = UUID.randomUUID().toString();
    private String customerId;
    private String customerEmail;
    private final List<OrderItem> items = new ArrayList<>();
    private double totalAmount = 0.0;
    private String status = "CREATED";
    private String pickupSlot = "18:00 - 19:00";
    private String pickupAddress = "Main Bakery Depot, Sector 4";
    private String paymentMethod = "UPI";
    private String paymentStatus = "PAID";
    private Instant createdAt = Instant.now();

    public OrderBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public OrderBuilder withCustomer(String customerId, String customerEmail) {
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        return this;
    }

    public OrderBuilder addItem(OrderItem item) {
        this.items.add(item);
        this.totalAmount += item.getSubtotal();
        return this;
    }

    public OrderBuilder withItems(List<OrderItem> items) {
        this.items.clear();
        this.totalAmount = 0.0;
        if (items != null) {
            for (OrderItem item : items) {
                addItem(item);
            }
        }
        return this;
    }

    public OrderBuilder withTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public OrderBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public OrderBuilder withPickupSlot(String pickupSlot) {
        this.pickupSlot = pickupSlot;
        return this;
    }

    public OrderBuilder withPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
        return this;
    }

    public OrderBuilder withPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderBuilder withPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public OrderBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Order build() {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalStateException("Customer ID is required to build an Order.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must contain at least one item.");
        }

        Order order = new Order(
            id, customerId, customerEmail, new ArrayList<>(items),
            totalAmount, status, pickupSlot, pickupAddress,
            paymentMethod, paymentStatus, createdAt
        );

        PatternTracer.getInstance().record(
            "Builder",
            "OrderBuilder",
            "build",
            "Constructed Order " + id + " for " + customerEmail + " (" + items.size() + " items, ₹" + totalAmount + ")"
        );

        return order;
    }
}

