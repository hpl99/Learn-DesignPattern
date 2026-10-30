package com.savebite.patterns;

import com.savebite.model.Listing;
import com.savebite.patterns.behavioral.chainofresponsibility.SecurityContext;
import com.savebite.patterns.behavioral.chainofresponsibility.SecurityPipeline;
import com.savebite.patterns.behavioral.command.*;
import com.savebite.patterns.behavioral.interpreter.ComparisonExpression;
import com.savebite.patterns.behavioral.interpreter.Expression;
import com.savebite.patterns.behavioral.interpreter.Parser;
import com.savebite.patterns.behavioral.iterator.CarouselIterator;
import com.savebite.patterns.behavioral.iterator.CustomIterator;
import com.savebite.patterns.behavioral.iterator.ListingAggregate;
import com.savebite.patterns.behavioral.mediator.CheckoutMediator;
import com.savebite.patterns.behavioral.memento.ProfileEditor;
import com.savebite.patterns.behavioral.memento.ProfileSnapshot;
import com.savebite.patterns.behavioral.memento.SnapshotHistory;
import com.savebite.patterns.behavioral.observer.CartBadgeObserver;
import com.savebite.patterns.behavioral.observer.PriceDropAlertObserver;
import com.savebite.patterns.behavioral.observer.PriceDropSubject;
import com.savebite.patterns.behavioral.state.FreshState;
import com.savebite.patterns.behavioral.state.LastChanceState;
import com.savebite.patterns.behavioral.state.ListingContext;
import com.savebite.patterns.behavioral.state.SoldState;
import com.savebite.patterns.behavioral.strategy.BulkBuyDiscountStrategy;
import com.savebite.patterns.behavioral.strategy.ExpiryBasedDiscountStrategy;
import com.savebite.patterns.behavioral.strategy.FlatDiscountStrategy;
import com.savebite.patterns.behavioral.strategy.PricingContext;
import com.savebite.patterns.behavioral.templatemethod.CardPayment;
import com.savebite.patterns.behavioral.templatemethod.PaymentProcessor;
import com.savebite.patterns.behavioral.templatemethod.UpiPayment;
import com.savebite.patterns.behavioral.visitor.ImpactVisitor;
import com.savebite.patterns.creational.builder.ListingBuilder;
import com.savebite.patterns.structural.composite.Category;
import com.savebite.patterns.structural.composite.ProductLeaf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BehavioralPatternsTest {

    // ==========================================
    // 13. CHAIN OF RESPONSIBILITY TEST
    // ==========================================
    @Test
    @DisplayName("Chain of Responsibility: executes handlers sequentially, stopping on first violation")
    void testChainOfResponsibility() {
        SecurityPipeline pipeline = new SecurityPipeline();

        // 1. Unauthenticated request to /api/admin/users must terminate at Step 5 with 401
        SecurityContext ctxAdmin = new SecurityContext("/api/admin/users", "GET", "127.0.0.1", new HashMap<>());
        pipeline.execute(ctxAdmin);
        assertThat(ctxAdmin.isTerminated()).isTrue();
        assertThat(ctxAdmin.getHttpStatus()).isEqualTo(401);

        // 2. Public request passes through cleanly with Request ID assigned
        SecurityContext ctxPublic = new SecurityContext("/api/listings", "GET", "127.0.0.1", new HashMap<>());
        pipeline.execute(ctxPublic);
        assertThat(ctxPublic.isTerminated()).isFalse();
        assertThat(ctxPublic.getHeader("x-request-id")).isNotNull();
    }

    // ==========================================
    // 14. COMMAND TEST
    // ==========================================
    @Test
    @DisplayName("Command: cart modifications support undo and redo (Ctrl+Z / Ctrl+Y)")
    void testCommand() {
        Cart cart = new Cart();
        CommandHistory history = new CommandHistory();
        CartItem item = new CartItem("l1", "Croissant", 40.0, 2, 0.2);

        // Execute add
        history.execute(new AddToCartCommand(cart, item));
        assertThat(cart.getTotalCount()).isEqualTo(2);
        assertThat(cart.getTotalPrice()).isEqualTo(80.0);

        // Undo add
        assertThat(history.canUndo()).isTrue();
        history.undo();
        assertThat(cart.getTotalCount()).isEqualTo(0);

        // Redo add
        assertThat(history.canRedo()).isTrue();
        history.redo();
        assertThat(cart.getTotalCount()).isEqualTo(2);

        // Execute remove
        history.execute(new RemoveFromCartCommand(cart, "l1", 1));
        assertThat(cart.getTotalCount()).isEqualTo(1);
        history.undo();
        assertThat(cart.getTotalCount()).isEqualTo(2);
    }

    // ==========================================
    // 15. INTERPRETER TEST
    // ==========================================
    @Test
    @DisplayName("Interpreter: parses search mini-language into AST and evaluates compound AND/OR expressions")
    void testInterpreter() {
        Parser parser = new Parser("price<100 AND category:bakery");
        Expression ast = parser.parse();

        Listing matching = new ListingBuilder()
            .withTitle("Bread")
            .withPricing(80, 80)
            .withCategory("bakery")
            .build();

        Listing wrongCategory = new ListingBuilder()
            .withTitle("Salad")
            .withPricing(80, 80)
            .withCategory("produce")
            .build();

        Listing tooExpensive = new ListingBuilder()
            .withTitle("Luxury Cake")
            .withPricing(150, 150)
            .withCategory("bakery")
            .build();

        assertThat(ast.interpret(matching)).isTrue();
        assertThat(ast.interpret(wrongCategory)).isFalse();
        assertThat(ast.interpret(tooExpensive)).isFalse();

        // OR expression test
        Parser orParser = new Parser("category:dairy OR category:bakery");
        Expression orAst = orParser.parse();
        assertThat(orAst.interpret(matching)).isTrue();
    }

    // ==========================================
    // 16. ITERATOR TEST
    // ==========================================
    @Test
    @DisplayName("Iterator: paginates listings chunk by chunk and provides circular carousel stepping")
    void testIterator() {
        ListingAggregate aggregate = new ListingAggregate();
        for (int i = 1; i <= 5; i++) {
            aggregate.add(new ListingBuilder().withTitle("Item " + i).withPricing(10 * i, 10 * i).build());
        }

        // Paging: 2 items per page -> 3 pages total (2, 2, 1)
        CustomIterator<List<Listing>> pageIterator = aggregate.createPageIterator(2);
        assertThat(pageIterator.hasNext()).isTrue();
        List<Listing> p1 = pageIterator.next();
        assertThat(p1).hasSize(2);
        assertThat(p1.get(0).getTitle()).isEqualTo("Item 1");

        List<Listing> p2 = pageIterator.next();
        assertThat(p2).hasSize(2);

        List<Listing> p3 = pageIterator.next();
        assertThat(p3).hasSize(1);
        assertThat(pageIterator.hasNext()).isFalse();

        // Carousel circular stepping
        CarouselIterator carousel = aggregate.createCarouselIterator();
        assertThat(carousel.next().getTitle()).isEqualTo("Item 1");
        assertThat(carousel.next().getTitle()).isEqualTo("Item 2");
        assertThat(carousel.previous().getTitle()).isEqualTo("Item 1");
    }

    // ==========================================
    // 17. MEDIATOR TEST
    // ==========================================
    @Test
    @DisplayName("Mediator: selecting country cascades to states, cities, hubs, and pickup slots")
    void testMediator() {
        CheckoutMediator mediator = new CheckoutMediator();
        assertThat(mediator.getCountryField().getValue()).isEqualTo("India");
        assertThat(mediator.getStateField().getValue()).isEqualTo("Karnataka");
        assertThat(mediator.getCityField().getValue()).isEqualTo("Bangalore");

        // Change country to United Kingdom
        mediator.getCountryField().setValue("United Kingdom");
        assertThat(mediator.getStateField().getValue()).isEqualTo("Greater London");
        assertThat(mediator.getCityField().getValue()).isEqualTo("London");
    }

    // ==========================================
    // 18. MEMENTO TEST
    // ==========================================
    @Test
    @DisplayName("Memento: Originator captures state snapshots and restores exact previous state")
    void testMemento() {
        ProfileEditor editor = new ProfileEditor("Priya Sharma", "+91 9988776655", "Indiranagar", "Veg", true);
        SnapshotHistory history = new SnapshotHistory();

        // Take snapshot 1
        ProfileSnapshot snap1 = editor.createSnapshot("Original Profile");
        history.push(snap1);

        // Mutate editor state
        editor.setFullName("Priya S. (Edited)");
        editor.setAddress("Bellandur Flat 101");
        editor.setDietaryPreference("Vegan");

        assertThat(editor.getFullName()).isEqualTo("Priya S. (Edited)");
        assertThat(editor.getAddress()).isEqualTo("Bellandur Flat 101");

        // Restore snapshot 1
        editor.restore(snap1);
        assertThat(editor.getFullName()).isEqualTo("Priya Sharma");
        assertThat(editor.getAddress()).isEqualTo("Indiranagar");
        assertThat(editor.getDietaryPreference()).isEqualTo("Veg");
    }

    // ==========================================
    // 19. OBSERVER TEST
    // ==========================================
    @Test
    @DisplayName("Observer: publishing price drops notifies badge, alert subscriber, and notification center")
    void testObserver() {
        PriceDropSubject subject = new PriceDropSubject();
        CartBadgeObserver badgeObs = new CartBadgeObserver();
        PriceDropAlertObserver alertObs = new PriceDropAlertObserver();

        subject.attach(badgeObs);
        subject.attach(alertObs);

        subject.notifyObservers("PRICE_DROP", new PriceDropSubject.PriceDropEvent("l1", "Sourdough", 180.0, 90.0, "DISCOUNTED"));

        assertThat(alertObs.getReceivedAlerts()).hasSize(1);
        assertThat(alertObs.getReceivedAlerts().get(0)).contains("Sourdough").contains("₹90");

        // Detach observer
        subject.detach(alertObs);
        subject.notifyObservers("PRICE_DROP", new PriceDropSubject.PriceDropEvent("l2", "Croissant", 100.0, 50.0, "DISCOUNTED"));
        assertThat(alertObs.getReceivedAlerts()).hasSize(1); // No new alert received
    }

    // ==========================================
    // 20. STATE TEST
    // ==========================================
    @Test
    @DisplayName("State: validates lifecycle transitions and strictly rejects illegal state mutations")
    void testState() {
        Listing listing = new ListingBuilder().withTitle("Baguette").withPricing(100, 100).withState("FRESH").build();
        ListingContext ctx = new ListingContext(listing);

        assertThat(ctx.getState()).isInstanceOf(FreshState.class);
        assertThat(ctx.canPurchase()).isTrue();

        // Tick to < 2 hours -> moves to LAST_CHANCE
        ctx.tickExpiry(60);
        assertThat(ctx.getState()).isInstanceOf(LastChanceState.class);

        // Mark Sold
        ctx.markSold();
        assertThat(ctx.getState()).isInstanceOf(SoldState.class);
        assertThat(ctx.canPurchase()).isFalse();

        // Illegal transition: cannot discount a SOLD listing!
        assertThatThrownBy(() -> ctx.applyDiscount(20.0))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot apply discount to a SOLD listing");
    }

    // ==========================================
    // 21. STRATEGY TEST
    // ==========================================
    @Test
    @DisplayName("Strategy: dynamically computes side-by-side pricing via interchangeable algorithms")
    void testStrategy() {
        Listing listing = new ListingBuilder()
            .withTitle("Artisan Loaf")
            .withPricing(200.0, 200.0)
            .withExpiry(Instant.now().plusSeconds(3600)) // 1 hour remaining -> 75% off
            .withQuantity(10)
            .build();

        PricingContext ctx = new PricingContext(new ExpiryBasedDiscountStrategy());
        double expiryPrice = ctx.calculatePrice(listing);
        assertThat(expiryPrice).isEqualTo(50.0); // 75% off 200 -> 50

        ctx.setStrategy(new FlatDiscountStrategy(40.0));
        double flatPrice = ctx.calculatePrice(listing);
        assertThat(flatPrice).isEqualTo(120.0); // 40% off 200 -> 120

        ctx.setStrategy(new BulkBuyDiscountStrategy());
        double bulkPrice = ctx.calculatePrice(listing);
        assertThat(bulkPrice).isEqualTo(60.0); // 10 units -> 70% off 200 -> 60
    }

    // ==========================================
    // 22. TEMPLATE METHOD TEST
    // ==========================================
    @Test
    @DisplayName("Template Method: enforces invariant validate -> authorize -> capture -> receipt skeleton")
    void testTemplateMethod() {
        PaymentProcessor card = new CardPayment();
        PaymentProcessor upi = new UpiPayment();

        // Valid card payment
        var res1 = card.processPayment("ord-1", 100.0, "4111222233334444");
        assertThat(res1.success()).isTrue();
        assertThat(res1.paymentMethod()).isEqualTo("CARD");
        assertThat(res1.receipt()).contains("AuthCode: AUTH-CARD-");

        // Invalid card payment (validation step fails)
        var res2 = card.processPayment("ord-2", 100.0, "short");
        assertThat(res2.success()).isFalse();
        assertThat(res2.message()).contains("validation failed");

        // Valid UPI payment
        var res3 = upi.processPayment("ord-3", 100.0, "priya@okaxis");
        assertThat(res3.success()).isTrue();
        assertThat(res3.receipt()).contains("UPI-NPCI-");
    }

    // ==========================================
    // 23. VISITOR TEST
    // ==========================================
    @Test
    @DisplayName("Visitor: double-dispatch tree traversal calculates kg rescued, rupees saved, and CO2 avoided")
    void testVisitor() {
        Category root = new Category("Marketplace");
        root.add(new ProductLeaf("l1", "Sourdough", 100.0, 1.0));
        root.add(new ProductLeaf("l2", "Cheese Pack", 200.0, 0.5));

        ImpactVisitor visitor = new ImpactVisitor();
        root.accept(visitor);

        // Weight = 1.0 + 0.5 = 1.5kg
        assertThat(visitor.getTotalKgRescued()).isEqualTo(1.5);
        // Rupees saved: (100*0.5) + (200*0.5) = 150.0
        assertThat(visitor.getTotalRupeesSaved()).isEqualTo(150.0);
        // CO2: 1.5kg * 2.5 factor = 3.75kg
        assertThat(visitor.getTotalCo2AvoidedKg()).isEqualTo(3.75);
    }
}

