package com.savebite.controller;

import com.savebite.patterns.behavioral.command.Cart;
import com.savebite.patterns.behavioral.command.CommandHistory;
import com.savebite.patterns.behavioral.mediator.CheckoutMediator;
import com.savebite.patterns.structural.facade.CheckoutFacade;
import io.javalin.http.Context;

import java.util.Map;

public class CheckoutController {
    private final CheckoutFacade checkoutFacade;
    private final CartController cartController;
    private final CheckoutMediator mediator = new CheckoutMediator();

    public CheckoutController(CheckoutFacade checkoutFacade, CartController cartController) {
        this.checkoutFacade = checkoutFacade;
        this.cartController = cartController;
    }

    public void getFormState(Context ctx) {
        ctx.json(mediator.getFormState());
    }

    public void updateFormField(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String field = body.get("field");
        String value = body.get("value");

        if ("country".equalsIgnoreCase(field)) {
            mediator.getCountryField().setValue(value);
        } else if ("state".equalsIgnoreCase(field)) {
            mediator.getStateField().setValue(value);
        } else if ("city".equalsIgnoreCase(field)) {
            mediator.getCityField().setValue(value);
        } else if ("pickupPoint".equalsIgnoreCase(field)) {
            mediator.getPickupPointField().setValue(value);
        } else if ("slot".equalsIgnoreCase(field)) {
            mediator.getSlotField().setValue(value);
        } else if ("payment".equalsIgnoreCase(field)) {
            mediator.getPaymentField().setValue(value);
        }

        ctx.json(mediator.getFormState());
    }

    public void processCheckout(Context ctx) {
        Cart cart = cartController.getCartForContext(ctx);
        CommandHistory history = cartController.getHistoryForContext(ctx);

        Map<String, Object> body = ctx.bodyAsClass(Map.class);
        String customerId = ctx.attribute("userId");
        String customerEmail = ctx.attribute("userEmail");

        if (customerId == null) {
            customerId = (String) body.getOrDefault("customerId", "cust-guest");
            customerEmail = (String) body.getOrDefault("customerEmail", "guest@savebite.org");
        }

        CheckoutFacade.CheckoutRequest req = new CheckoutFacade.CheckoutRequest(
            customerId,
            customerEmail,
            (String) body.getOrDefault("pickupSlot", "18:00 - 19:00"),
            (String) body.getOrDefault("pickupAddress", "Main Bakery Hub"),
            (String) body.getOrDefault("paymentMethod", "UPI"),
            (String) body.getOrDefault("paymentDetails", "priya@okaxis"),
            (String) body.get("couponCode"),
            Boolean.parseBoolean(String.valueOf(body.getOrDefault("includeGiftWrap", false))),
            Boolean.parseBoolean(String.valueOf(body.getOrDefault("isPrioritySlot", false)))
        );

        CheckoutFacade.CheckoutResponse response = checkoutFacade.checkout(cart, history, req);
        if (!response.success()) {
            ctx.status(400).json(Map.of("error", response.errorMessage()));
        } else {
            ctx.json(response);
        }
    }
}

