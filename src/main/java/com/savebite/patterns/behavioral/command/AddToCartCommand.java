package com.savebite.patterns.behavioral.command;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Command
// Category: Behavioral
// Problem it solves here: Encapsulates adding a product to cart as a command with precise reverse undo logic.
// GoF participant role: ConcreteCommand
// Used by: CommandHistory
@DesignPattern(name = "Command", role = "ConcreteCommand", description = "Command adding items to cart")
public class AddToCartCommand implements Command {
    private final Cart cart;
    private final CartItem item;

    public AddToCartCommand(Cart cart, CartItem item) {
        this.cart = cart;
        this.item = item;
    }

    @Override
    public void execute() {
        cart.addItem(item);
        PatternTracer.getInstance().record(
            "Command",
            "AddToCartCommand",
            "execute",
            "Executed: Add " + item.getQuantity() + "x '" + item.getTitle() + "' to cart"
        );
    }

    @Override
    public void undo() {
        cart.removeItem(item.getListingId(), item.getQuantity());
        PatternTracer.getInstance().record(
            "Command",
            "AddToCartCommand",
            "undo",
            "Undid: Removed " + item.getQuantity() + "x '" + item.getTitle() + "' from cart"
        );
    }

    @Override
    public String getDescription() {
        return "Add " + item.getQuantity() + "x " + item.getTitle();
    }
}

