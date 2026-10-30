package com.savebite.patterns.behavioral.command;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Pattern: Command
// Category: Behavioral
// Problem it solves here: The Cart receiver maintaining current selected items. State mutations are encapsulated into reversible Command objects.
// GoF participant role: Receiver
// Used by: AddToCartCommand, RemoveFromCartCommand, ClearCartCommand
@DesignPattern(name = "Command", role = "Receiver", description = "Shopping cart receiver holding current items")
public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    public synchronized void addItem(CartItem item) {
        Optional<CartItem> existing = items.stream()
            .filter(i -> i.getListingId().equals(item.getListingId()))
            .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + item.getQuantity());
        } else {
            items.add(new CartItem(item.getListingId(), item.getTitle(), item.getUnitPrice(), item.getQuantity(), item.getWeightKg()));
        }

        PatternTracer.getInstance().record(
            "Command",
            "Cart",
            "addItem",
            "Added " + item.getQuantity() + "x '" + item.getTitle() + "' to cart. Total items: " + getTotalCount()
        );
    }

    public synchronized void removeItem(String listingId, int quantity) {
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            if (item.getListingId().equals(listingId)) {
                if (item.getQuantity() <= quantity) {
                    items.remove(i);
                } else {
                    item.setQuantity(item.getQuantity() - quantity);
                }
                break;
            }
        }
        PatternTracer.getInstance().record(
            "Command",
            "Cart",
            "removeItem",
            "Removed listing " + listingId + " from cart. Total items: " + getTotalCount()
        );
    }

    public synchronized void clear() {
        items.clear();
        PatternTracer.getInstance().record("Command", "Cart", "clear", "Cart cleared");
    }

    public synchronized List<CartItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public synchronized double getTotalPrice() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public synchronized int getTotalCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public synchronized double getTotalWeightKg() {
        return items.stream().mapToDouble(i -> i.getWeightKg() * i.getQuantity()).sum();
    }
}

