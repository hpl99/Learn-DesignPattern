package com.savebite.patterns.creational.prototype;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Order;
import com.savebite.model.OrderItem;
import com.savebite.teaching.PatternTracer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Pattern: Prototype
// Category: Creational
// Problem it solves here: Enables 1-click re-ordering by deep copying all order items and configuration while generating fresh order ID, pending payment status, and new timestamp.
// GoF participant role: ConcretePrototype
// Used by: ReorderService, OrderController
@DesignPattern(name = "Prototype", role = "ConcretePrototype", description = "Clonable order prototype with deep items copy")
public class OrderPrototype implements Prototype<Order> {
    private final Order originalOrder;

    public OrderPrototype(Order originalOrder) {
        this.originalOrder = originalOrder;
    }

    @Override
    public Order copy() {
        String newOrderId = UUID.randomUUID().toString();
        List<OrderItem> clonedItems = new ArrayList<>();

        for (OrderItem item : originalOrder.getItems()) {
            OrderItem clonedItem = new OrderItem(
                UUID.randomUUID().toString(),
                newOrderId,
                item.getListingId(),
                item.getTitle(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal(),
                item.getWeightKg()
            );
            clonedItems.add(clonedItem);
        }

        Order clonedOrder = new Order(
            newOrderId,
            originalOrder.getCustomerId(),
            originalOrder.getCustomerEmail(),
            clonedItems,
            originalOrder.getTotalAmount(),
            "CREATED",
            originalOrder.getPickupSlot(),
            originalOrder.getPickupAddress(),
            originalOrder.getPaymentMethod(),
            "PENDING",
            Instant.now()
        );

        PatternTracer.getInstance().record(
            "Prototype",
            "OrderPrototype",
            "copy",
            "Cloned past Order [" + originalOrder.getId() + "] -> New Order [" + clonedOrder.getId() + "] with " + clonedItems.size() + " items"
        );

        return clonedOrder;
    }

    public Order getOriginalOrder() {
        return originalOrder;
    }
}

