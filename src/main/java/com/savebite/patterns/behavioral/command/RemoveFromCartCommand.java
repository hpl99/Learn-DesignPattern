package com.savebite.patterns.behavioral.command;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.Optional;

// Pattern: Command
// Category: Behavioral
// Problem it solves here: Encapsulates removing items from cart, saving prior state for undo restoration.
// GoF participant role: ConcreteCommand
// Used by: CommandHistory
@DesignPattern(name = "Command", role = "ConcreteCommand", description = "Command removing items from cart with restoration")
public class RemoveFromCartCommand implements Command {
    private final Cart cart;
    private final String listingId;
    private final int quantity;
    private CartItem removedBackup;

    public RemoveFromCartCommand(Cart cart, String listingId, int quantity) {
        this.cart = cart;
        this.listingId = listingId;
        this.quantity = quantity;
    }

    @Override
    public void execute() {
        Optional<CartItem> found = cart.getItems().stream()
            .filter(i -> i.getListingId().equals(listingId))
            .findFirst();

        if (found.isPresent()) {
            CartItem current = found.get();
            this.removedBackup = new CartItem(
                current.getListingId(),
                current.getTitle(),
                current.getUnitPrice(),
                Math.min(quantity, current.getQuantity()),
                current.getWeightKg()
            );
            cart.removeItem(listingId, quantity);
            PatternTracer.getInstance().record(
                "Command",
                "RemoveFromCartCommand",
                "execute",
                "Executed: Remove item " + listingId + " (qty=" + quantity + ") from cart"
            );
        }
    }

    @Override
    public void undo() {
        if (removedBackup != null) {
            cart.addItem(removedBackup);
            PatternTracer.getInstance().record(
                "Command",
                "RemoveFromCartCommand",
                "undo",
                "Undid: Restored " + removedBackup.getQuantity() + "x '" + removedBackup.getTitle() + "' to cart"
            );
        }
    }

    @Override
    public String getDescription() {
        return "Remove item " + listingId + " (qty " + quantity + ")";
    }
}

