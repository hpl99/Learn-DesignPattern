package com.savebite.patterns.structural.facade;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.model.Order;
import com.savebite.model.OrderItem;
import com.savebite.patterns.behavioral.command.Cart;
import com.savebite.patterns.behavioral.command.CartItem;
import com.savebite.patterns.behavioral.command.ClearCartCommand;
import com.savebite.patterns.behavioral.command.CommandHistory;
import com.savebite.patterns.behavioral.templatemethod.PaymentProcessor;
import com.savebite.patterns.creational.builder.OrderBuilder;
import com.savebite.patterns.creational.factorymethod.NotificationChannel;
import com.savebite.patterns.creational.factorymethod.NotificationFactory;
import com.savebite.patterns.structural.decorator.BasePrice;
import com.savebite.patterns.structural.decorator.CouponDecorator;
import com.savebite.patterns.structural.decorator.GiftWrapDecorator;
import com.savebite.patterns.structural.decorator.PickupPriorityDecorator;
import com.savebite.patterns.structural.decorator.Price;
import com.savebite.repository.ListingRepository;
import com.savebite.repository.OrderRepository;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Pattern: Facade
// Category: Structural
// Problem it solves here: The reservation and checkout process involves 7 distinct subsystems (inventory verification, pricing decorators, payment template processing, order construction, inventory deduction, notification factory dispatch, and cart clearing). CheckoutFacade orchestrates them all behind a single, atomic method call.
// GoF participant role: Facade
// Used by: CheckoutController
@DesignPattern(name = "Facade", role = "Facade", description = "Single unified facade orchestrating checkout subsystems")
public class CheckoutFacade {
    private final ListingRepository listingRepository;
    private final OrderRepository orderRepository;
    private final Map<String, PaymentProcessor> paymentProcessors;
    private final NotificationFactory notificationFactory;

    public record CheckoutRequest(
        String customerId,
        String customerEmail,
        String pickupSlot,
        String pickupAddress,
        String paymentMethod,
        String paymentDetails,
        String couponCode,
        boolean includeGiftWrap,
        boolean isPrioritySlot
    ) {}

    public record CheckoutResponse(
        boolean success,
        String orderId,
        double totalAmount,
        String priceBreakdown,
        String receipt,
        String errorMessage
    ) {}

    public CheckoutFacade(
        ListingRepository listingRepository,
        OrderRepository orderRepository,
        Map<String, PaymentProcessor> paymentProcessors,
        NotificationFactory notificationFactory
    ) {
        this.listingRepository = listingRepository;
        this.orderRepository = orderRepository;
        this.paymentProcessors = paymentProcessors;
        this.notificationFactory = notificationFactory;
    }

    public synchronized CheckoutResponse checkout(Cart cart, CommandHistory cartHistory, CheckoutRequest request) {
        PatternTracer.getInstance().record(
            "Facade",
            "CheckoutFacade",
            "checkout",
            "Initiating unified checkout transaction for " + request.customerEmail()
        );

        if (cart.getItems().isEmpty()) {
            return new CheckoutResponse(false, null, 0.0, "", null, "Cart is empty");
        }

        // Subsystem 1: Inventory Stock Verification
        for (CartItem item : cart.getItems()) {
            Optional<Listing> listingOpt = listingRepository.findById(item.getListingId());
            if (listingOpt.isEmpty() || listingOpt.get().getQuantity() < item.getQuantity()) {
                String title = listingOpt.map(Listing::getTitle).orElse(item.getTitle());
                return new CheckoutResponse(false, null, 0.0, "", null, "Insufficient stock for: " + title);
            }
        }

        // Subsystem 2: Dynamic Price Decorator Pipeline
        double rawBasePrice = cart.getTotalPrice();
        Price pricePipeline = new BasePrice(rawBasePrice, "Subtotal (" + cart.getTotalCount() + " items)");

        if (request.includeGiftWrap()) {
            pricePipeline = new GiftWrapDecorator(pricePipeline);
        }
        if (request.isPrioritySlot()) {
            pricePipeline = new PickupPriorityDecorator(pricePipeline);
        }
        if (request.couponCode() != null && !request.couponCode().isBlank()) {
            pricePipeline = new CouponDecorator(pricePipeline, request.couponCode(), 15.0); // 15% promo discount
        }

        double finalAmount = pricePipeline.getAmount();
        String priceBreakdown = pricePipeline.getDescription();

        // Subsystem 3: Payment Template Method Processing
        String method = request.paymentMethod() != null ? request.paymentMethod().toUpperCase() : "UPI";
        PaymentProcessor processor = paymentProcessors.getOrDefault(method, paymentProcessors.get("UPI"));

        String orderId = "ord-" + UUID.randomUUID().toString().substring(0, 8);
        PaymentProcessor.PaymentResult paymentResult = processor.processPayment(
            orderId,
            finalAmount,
            request.paymentDetails() != null ? request.paymentDetails() : "default"
        );

        if (!paymentResult.success()) {
            return new CheckoutResponse(false, null, finalAmount, priceBreakdown, null, "Payment failed: " + paymentResult.message());
        }

        // Subsystem 4: Order Builder Construction
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            orderItems.add(new OrderItem(
                UUID.randomUUID().toString(),
                orderId,
                ci.getListingId(),
                ci.getTitle(),
                ci.getQuantity(),
                ci.getUnitPrice(),
                ci.getSubtotal(),
                ci.getWeightKg()
            ));
        }

        Order order = new OrderBuilder()
            .withId(orderId)
            .withCustomer(request.customerId(), request.customerEmail())
            .withItems(orderItems)
            .withTotalAmount(finalAmount)
            .withStatus("CONFIRMED")
            .withPickupSlot(request.pickupSlot() != null ? request.pickupSlot() : "18:00 - 19:00")
            .withPickupAddress(request.pickupAddress() != null ? request.pickupAddress() : "Main Bakery Hub")
            .withPaymentMethod(method)
            .withPaymentStatus("PAID")
            .build();

        orderRepository.save(order);

        // Subsystem 5: Deduct Listing Quantities
        for (CartItem ci : cart.getItems()) {
            listingRepository.findById(ci.getListingId()).ifPresent(listing -> {
                int newQty = Math.max(0, listing.getQuantity() - ci.getQuantity());
                listingRepository.updateQuantity(listing.getId(), newQty);
                if (newQty == 0) {
                    listingRepository.updatePricingAndState(listing.getId(), listing.getCurrentPrice(), "SOLD");
                }
            });
        }

        // Subsystem 6: Notification Factory Dispatch
        notificationFactory.createNotification(NotificationChannel.TOAST).send(
            request.customerEmail(),
            "Order #" + orderId + " reserved successfully! Collect at: " + order.getPickupSlot()
        );

        // Subsystem 7: Clear Cart via Command
        if (cartHistory != null) {
            cartHistory.execute(new ClearCartCommand(cart));
        } else {
            cart.clear();
        }

        PatternTracer.getInstance().record(
            "Facade",
            "CheckoutFacade",
            "checkout",
            "Unified checkout completed successfully! Order ID: " + orderId + ", Total: ₹" + finalAmount
        );

        return new CheckoutResponse(true, orderId, finalAmount, priceBreakdown, paymentResult.receipt(), null);
    }
}

