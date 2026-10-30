package com.savebite.patterns.behavioral.command;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.List;

// Pattern: Command
// Category: Behavioral
// Problem it solves here: Encapsulates clearing the entire cart while saving snapshot of all items to allow undoing accidental cart wipes.
// GoF participant role: ConcreteCommand
// Used by: CommandHistory
@DesignPattern(name = "Command", role = "ConcreteCommand", description = "Reversible clear-cart command")
public class ClearCartCommand implements Command {
    private final Cart cart;
    private final List<CartItem> previousItems = new ArrayList<>();

    public ClearCartCommand(Cart cart) {
        this.cart = cart;
    }

    @Override
    public void execute() {
        previousItems.clear();
        previousItems.addAll(cart.getItems());
        cart.clear();
        PatternTracer.getInstance().record(
            "Command",
            "ClearCartCommand",
            "execute",
            "Executed: Cleared cart of " + previousItems.size() + " items"
        );
    }

    @Override
    public void undo() {
        for (CartItem item : previousItems) {
            cart.addItem(item);
        }
        PatternTracer.getInstance().record(
            "Command",
            "ClearCartCommand",
            "undo",
            "Undid: Restored " + previousItems.size() + " items back into cart"
        );
    }

    @Override
    public String getDescription() {
        return "Clear all items (" + previousItems.size() + " items)";
    }
}

