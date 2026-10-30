package com.savebite.patterns.creational.prototype;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Order;
import com.savebite.teaching.PatternTracer;

// Pattern: Prototype
// Category: Creational
// Problem it solves here: Service that utilizes prototype cloning to allow customers to instantly repeat favorite past rescue orders.
// GoF participant role: Client
// Used by: OrderController
@DesignPattern(name = "Prototype", role = "Client", description = "Client service executing order clones via Prototype")
public class ReorderService {

    public Order reorder(Order pastOrder) {
        if (pastOrder == null) {
            throw new IllegalArgumentException("Cannot reorder null order");
        }

        OrderPrototype prototype = new OrderPrototype(pastOrder);
        Order reordered = prototype.copy();

        PatternTracer.getInstance().record(
            "Prototype",
            "ReorderService",
            "reorder",
            "Customer successfully reordered past purchase. New order generated: " + reordered.getId()
        );

        return reordered;
    }
}

