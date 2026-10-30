package com.savebite.controller;

import com.savebite.model.Order;
import com.savebite.service.OrderService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void getMyOrders(Context ctx) {
        String userId = ctx.attribute("userId");
        if (userId == null) {
            userId = "usr-cust-01"; // Fallback to demo customer
        }

        List<Order> orders = orderService.getCustomerOrders(userId);
        ctx.json(orders);
    }

    public void getOrderById(Context ctx) {
        String orderId = ctx.pathParam("id");
        Optional<Order> order = orderService.getOrderById(orderId);
        if (order.isPresent()) {
            ctx.json(order.get());
        } else {
            ctx.status(404).json(Map.of("error", "Order not found: " + orderId));
        }
    }

    public void reorder(Context ctx) {
        String orderId = ctx.pathParam("id");
        try {
            Order reordered = orderService.reorder(orderId);
            ctx.status(201).json(reordered);
        } catch (IllegalArgumentException e) {
            ctx.status(404).json(Map.of("error", e.getMessage()));
        }
    }

    public void updateStatus(Context ctx) {
        String orderId = ctx.pathParam("id");
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String newStatus = body.get("status");

        boolean updated = orderService.updateStatus(orderId, newStatus);
        if (updated) {
            ctx.json(Map.of("success", true, "status", newStatus));
        } else {
            ctx.status(404).json(Map.of("error", "Failed to update order status"));
        }
    }
}

