package com.savebite.controller;

import com.savebite.patterns.behavioral.command.*;
import com.savebite.patterns.behavioral.observer.Observer;
import io.javalin.http.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CartController {
    // User or session-scoped carts and histories
    private final Map<String, Cart> userCarts = new ConcurrentHashMap<>();
    private final Map<String, CommandHistory> userHistories = new ConcurrentHashMap<>();
    private final Observer cartBadgeObserver;

    public CartController(Observer cartBadgeObserver) {
        this.cartBadgeObserver = cartBadgeObserver;
    }

    private String getSessionKey(Context ctx) {
        String userId = ctx.attribute("userId");
        if (userId != null && !userId.isBlank()) {
            return userId;
        }
        String sessionCookie = ctx.cookie("savebite_cart_session");
        if (sessionCookie == null || sessionCookie.isBlank()) {
            sessionCookie = "sess-" + java.util.UUID.randomUUID().toString().substring(0, 8);
            ctx.cookie("savebite_cart_session", sessionCookie);
        }
        return sessionCookie;
    }

    public Cart getCartForContext(Context ctx) {
        return userCarts.computeIfAbsent(getSessionKey(ctx), k -> new Cart());
    }

    public CommandHistory getHistoryForContext(Context ctx) {
        return userHistories.computeIfAbsent(getSessionKey(ctx), k -> new CommandHistory());
    }

    public void getCart(Context ctx) {
        Cart cart = getCartForContext(ctx);
        CommandHistory history = getHistoryForContext(ctx);

        ctx.json(Map.of(
            "items", cart.getItems(),
            "totalCount", cart.getTotalCount(),
            "totalPrice", cart.getTotalPrice(),
            "totalWeightKg", cart.getTotalWeightKg(),
            "canUndo", history.canUndo(),
            "canRedo", history.canRedo(),
            "undoHistory", history.getUndoHistory()
        ));
    }

    public void addItem(Context ctx) {
        Cart cart = getCartForContext(ctx);
        CommandHistory history = getHistoryForContext(ctx);

        Map<String, Object> body = ctx.bodyAsClass(Map.class);
        String listingId = (String) body.get("listingId");
        String title = (String) body.get("title");
        double unitPrice = Double.parseDouble(String.valueOf(body.get("unitPrice")));
        int quantity = Integer.parseInt(String.valueOf(body.getOrDefault("quantity", 1)));
        double weightKg = Double.parseDouble(String.valueOf(body.getOrDefault("weightKg", 0.5)));

        CartItem item = new CartItem(listingId, title, unitPrice, quantity, weightKg);
        history.execute(new AddToCartCommand(cart, item));

        if (cartBadgeObserver != null) {
            cartBadgeObserver.update("CART_UPDATED", cart.getTotalCount());
        }

        getCart(ctx);
    }

    public void removeItem(Context ctx) {
        Cart cart = getCartForContext(ctx);
        CommandHistory history = getHistoryForContext(ctx);

        String listingId = ctx.pathParam("id");
        int quantity = ctx.queryParamAsClass("quantity", Integer.class).getOrDefault(1);

        history.execute(new RemoveFromCartCommand(cart, listingId, quantity));

        if (cartBadgeObserver != null) {
            cartBadgeObserver.update("CART_UPDATED", cart.getTotalCount());
        }

        getCart(ctx);
    }

    public void undo(Context ctx) {
        Cart cart = getCartForContext(ctx);
        CommandHistory history = getHistoryForContext(ctx);

        boolean success = history.undo();
        if (cartBadgeObserver != null) {
            cartBadgeObserver.update("CART_UPDATED", cart.getTotalCount());
        }

        ctx.json(Map.of("success", success, "message", success ? "Undid last cart action" : "Nothing to undo"));
    }

    public void redo(Context ctx) {
        Cart cart = getCartForContext(ctx);
        CommandHistory history = getHistoryForContext(ctx);

        boolean success = history.redo();
        if (cartBadgeObserver != null) {
            cartBadgeObserver.update("CART_UPDATED", cart.getTotalCount());
        }

        ctx.json(Map.of("success", success, "message", success ? "Redid action" : "Nothing to redo"));
    }

    public void clear(Context ctx) {
        Cart cart = getCartForContext(ctx);
        CommandHistory history = getHistoryForContext(ctx);

        history.execute(new ClearCartCommand(cart));

        if (cartBadgeObserver != null) {
            cartBadgeObserver.update("CART_UPDATED", 0);
        }

        getCart(ctx);
    }
}

