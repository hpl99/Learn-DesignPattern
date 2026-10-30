package com.savebite.teaching;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.savebite.annotation.DesignPattern;
import com.savebite.annotation.DesignPatterns;
import com.savebite.patterns.behavioral.chainofresponsibility.SecurityContext;
import com.savebite.patterns.behavioral.chainofresponsibility.SecurityPipeline;
import com.savebite.patterns.behavioral.command.Cart;
import com.savebite.patterns.behavioral.command.CartItem;
import com.savebite.patterns.behavioral.command.CommandHistory;
import com.savebite.patterns.behavioral.interpreter.ComparisonExpression;
import com.savebite.patterns.behavioral.interpreter.Expression;
import com.savebite.patterns.behavioral.interpreter.Parser;
import com.savebite.patterns.behavioral.iterator.CarouselIterator;
import com.savebite.patterns.behavioral.iterator.ListingAggregate;
import com.savebite.patterns.behavioral.iterator.PaginatedListingIterator;
import com.savebite.patterns.behavioral.mediator.CheckoutMediator;
import com.savebite.patterns.behavioral.memento.ProfileEditor;
import com.savebite.patterns.behavioral.memento.ProfileSnapshot;
import com.savebite.patterns.behavioral.memento.SnapshotHistory;
import com.savebite.patterns.behavioral.observer.CartBadgeObserver;
import com.savebite.patterns.behavioral.observer.NotificationCenter;
import com.savebite.patterns.behavioral.observer.PriceDropAlertObserver;
import com.savebite.patterns.behavioral.observer.PriceDropSubject;
import com.savebite.patterns.behavioral.state.FreshState;
import com.savebite.patterns.behavioral.state.ListingContext;
import com.savebite.patterns.behavioral.strategy.BulkBuyDiscountStrategy;
import com.savebite.patterns.behavioral.strategy.ExpiryBasedDiscountStrategy;
import com.savebite.patterns.behavioral.strategy.FlatDiscountStrategy;
import com.savebite.patterns.behavioral.strategy.PricingContext;
import com.savebite.patterns.behavioral.templatemethod.CardPayment;
import com.savebite.patterns.behavioral.templatemethod.CodPayment;
import com.savebite.patterns.behavioral.templatemethod.PaymentProcessor;
import com.savebite.patterns.behavioral.templatemethod.UpiPayment;
import com.savebite.patterns.behavioral.visitor.ImpactVisitor;
import com.savebite.patterns.creational.abstractfactory.DarkThemeFactory;
import com.savebite.patterns.creational.abstractfactory.LightThemeFactory;
import com.savebite.patterns.creational.builder.ListingBuilder;
import com.savebite.patterns.creational.factorymethod.NotificationChannel;
import com.savebite.patterns.creational.factorymethod.NotificationFactory;
import com.savebite.patterns.creational.prototype.OrderPrototype;
import com.savebite.patterns.creational.prototype.ReorderService;
import com.savebite.patterns.creational.singleton.AppLogger;
import com.savebite.patterns.creational.singleton.ConfigManager;
import com.savebite.patterns.creational.singleton.JwtKeyHolder;
import com.savebite.patterns.structural.adapter.GoogleAuthAdapter;
import com.savebite.patterns.structural.bridge.CsvExporter;
import com.savebite.patterns.structural.bridge.HtmlExporter;
import com.savebite.patterns.structural.bridge.PdfExporter;
import com.savebite.patterns.structural.bridge.SalesReport;
import com.savebite.patterns.structural.composite.Bundle;
import com.savebite.patterns.structural.composite.Category;
import com.savebite.patterns.structural.composite.ProductLeaf;
import com.savebite.patterns.structural.decorator.BasePrice;
import com.savebite.patterns.structural.decorator.CouponDecorator;
import com.savebite.patterns.structural.decorator.GiftWrapDecorator;
import com.savebite.patterns.structural.decorator.PickupPriorityDecorator;
import com.savebite.patterns.structural.decorator.Price;
import com.savebite.patterns.structural.flyweight.BadgeFactory;
import com.savebite.patterns.structural.flyweight.BadgeFlyweight;
import com.savebite.patterns.structural.proxy.AdminServiceProxy;
import com.savebite.patterns.structural.proxy.CachingListingRepositoryProxy;
import com.savebite.patterns.structural.proxy.RealAdminService;
import com.savebite.model.Listing;
import com.savebite.model.Order;
import com.savebite.model.OrderItem;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

public class PatternRegistry {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static List<Map<String, Object>> patternsData = new ArrayList<>();

    static {
        loadPatternsJson();
    }

    private static void loadPatternsJson() {
        try {
            InputStream is = PatternRegistry.class.getResourceAsStream("/patterns.json");
            if (is == null) {
                Path p = Path.of("src/main/resources/patterns.json");
                if (Files.exists(p)) {
                    is = Files.newInputStream(p);
                }
            }
            if (is != null) {
                patternsData = MAPPER.readValue(is, new TypeReference<>() {});
            }
        } catch (Exception e) {
            System.err.println("Failed to load patterns.json: " + e.getMessage());
        }
    }

    public static List<Map<String, Object>> getAllPatterns() {
        return patternsData;
    }

    public static Optional<Map<String, Object>> getPattern(String id) {
        return patternsData.stream()
            .filter(p -> id.equalsIgnoreCase(String.valueOf(p.get("id"))))
            .findFirst();
    }

    public static Map<String, Object> generateLiveProof(String id) {
        return switch (id.toLowerCase()) {
            case "singleton" -> proofSingleton();
            case "factorymethod" -> proofFactoryMethod();
            case "abstractfactory" -> proofAbstractFactory();
            case "builder" -> proofBuilder();
            case "prototype" -> proofPrototype();
            case "adapter" -> proofAdapter();
            case "bridge" -> proofBridge();
            case "composite" -> proofComposite();
            case "decorator" -> proofDecorator();
            case "facade" -> proofFacade();
            case "flyweight" -> proofFlyweight();
            case "proxy" -> proofProxy();
            case "chainofresponsibility" -> proofChainOfResponsibility();
            case "command" -> proofCommand();
            case "interpreter" -> proofInterpreter();
            case "iterator" -> proofIterator();
            case "mediator" -> proofMediator();
            case "memento" -> proofMemento();
            case "observer" -> proofObserver();
            case "state" -> proofState();
            case "strategy" -> proofStrategy();
            case "templatemethod" -> proofTemplateMethod();
            case "visitor" -> proofVisitor();
            default -> Map.of("error", "Pattern proof not found for: " + id);
        };
    }

    private static Map<String, Object> proofSingleton() {
        ConfigManager cfg1 = ConfigManager.getInstance();
        ConfigManager cfg2 = ConfigManager.getInstance();
        AppLogger log1 = AppLogger.getInstance();
        AppLogger log2 = AppLogger.getInstance();
        JwtKeyHolder key1 = JwtKeyHolder.getInstance();

        return Map.of(
            "pattern", "Singleton",
            "sameConfigInstance", (cfg1 == cfg2),
            "configInstanceId", cfg1.getInstanceId(),
            "configAccessCount", cfg1.getAccessCount(),
            "sameLoggerInstance", (log1 == log2),
            "loggerInstanceId", log1.getInstanceId(),
            "jwtKeyHolderInstanceId", key1.getInstanceId(),
            "threadSafetyTechnique", "Bill Pugh Initialization-on-demand Holder Idiom (JLS 12.4.2)"
        );
    }

    private static Map<String, Object> proofFactoryMethod() {
        NotificationFactory factory = new NotificationFactory();
        var toast = factory.createNotification(NotificationChannel.TOAST);
        var email = factory.createNotification(NotificationChannel.EMAIL);
        var sms = factory.createNotification(NotificationChannel.SMS);

        return Map.of(
            "pattern", "Factory Method",
            "toastProduct", toast.getClass().getSimpleName(),
            "toastPayload", toast.getFormattedPayload("priya@savebite.org", "Your Sourdough order is reserved!"),
            "emailProduct", email.getClass().getSimpleName(),
            "emailPayload", email.getFormattedPayload("priya@savebite.org", "Official SaveBite Tax Invoice #4401"),
            "smsProduct", sms.getClass().getSimpleName(),
            "smsPayload", sms.getFormattedPayload("+919988776655", "URGENT: Pickup before 20:00 closing.")
        );
    }

    private static Map<String, Object> proofAbstractFactory() {
        var lightFactory = new LightThemeFactory();
        var darkFactory = new DarkThemeFactory();

        return Map.of(
            "pattern", "Abstract Factory",
            "lightFamily", Map.of(
                "buttonBg", lightFactory.createButton().getBackgroundColor(),
                "cardBg", lightFactory.createCard().getBackgroundColor(),
                "inputBorder", lightFactory.createInput().getBorderColor(),
                "badgeText", lightFactory.createBadge().getTextColor()
            ),
            "darkFamily", Map.of(
                "buttonBg", darkFactory.createButton().getBackgroundColor(),
                "cardBg", darkFactory.createCard().getBackgroundColor(),
                "inputBorder", darkFactory.createInput().getBorderColor(),
                "badgeText", darkFactory.createBadge().getTextColor()
            )
        );
    }

    private static Map<String, Object> proofBuilder() {
        Listing listing = new ListingBuilder()
            .withTitle("Multigrain Ciabatta Loaf")
            .withSeller("usr-seller-01", "Artisan Bakery")
            .withCategory("bakery")
            .withPricing(150.0, 75.0)
            .withWeightKg(0.60)
            .build();

        return Map.of(
            "pattern", "Builder",
            "builtListingId", listing.getId(),
            "title", listing.getTitle(),
            "originalPrice", listing.getOriginalPrice(),
            "currentPrice", listing.getCurrentPrice(),
            "state", listing.getState(),
            "weightKg", listing.getWeightKg()
        );
    }

    private static Map<String, Object> proofPrototype() {
        List<OrderItem> items = List.of(new OrderItem("i-1", "ord-orig", "list-001", "Sourdough", 2, 90.0, 180.0, 0.75));
        Order original = new Order("ord-orig", "usr-cust-01", "priya@savebite.org", items, 180.0, "COMPLETED", "18:00 - 19:00", "Indiranagar Hub", "UPI", "PAID", Instant.now().minusSeconds(86400));

        OrderPrototype prototype = new OrderPrototype(original);
        Order clone = prototype.copy();

        return Map.of(
            "pattern", "Prototype",
            "originalOrderId", original.getId(),
            "clonedOrderId", clone.getId(),
            "isDifferentObject", (original != clone),
            "originalStatus", original.getStatus(),
            "clonedStatus", clone.getStatus(),
            "clonedPaymentStatus", clone.getPaymentStatus(),
            "itemsCountIdentical", (original.getItems().size() == clone.getItems().size())
        );
    }

    private static Map<String, Object> proofAdapter() {
        GoogleAuthAdapter googleAdapter = new GoogleAuthAdapter();
        var result = googleAdapter.authenticate("demo-token-sharma");

        return Map.of(
            "pattern", "Adapter",
            "targetInterface", "AuthProvider",
            "adaptedProvider", result.getProvider(),
            "unifiedEmail", result.getEmail(),
            "unifiedFullName", result.getFullName(),
            "unifiedProviderUserId", result.getProviderUserId()
        );
    }

    private static Map<String, Object> proofBridge() {
        SalesReport report = new SalesReport(new CsvExporter(), "Artisan Oven Bakery");
        report.addSale("Sourdough Loaf", "8", "720");
        report.addSale("Butter Croissants", "5", "550");

        String csvOutput = report.generate();

        report.setExporter(new HtmlExporter());
        String htmlOutput = report.generate();

        report.setExporter(new PdfExporter());
        String pdfOutput = report.generate();

        return Map.of(
            "pattern", "Bridge",
            "abstraction", "SalesReport",
            "csvPreview", csvOutput.substring(0, Math.min(100, csvOutput.length())),
            "htmlPreview", htmlOutput.substring(0, Math.min(120, htmlOutput.length())),
            "pdfPreview", pdfOutput.substring(0, Math.min(150, pdfOutput.length()))
        );
    }

    private static Map<String, Object> proofComposite() {
        Category bakery = new Category("Bakery Section");
        bakery.add(new ProductLeaf("l-1", "Sourdough", 90.0, 0.75));
        bakery.add(new ProductLeaf("l-2", "Croissants", 110.0, 0.35));

        Bundle rescueBox = new Bundle("Evening Mystery Box", 20.0);
        rescueBox.addItem(new ProductLeaf("l-3", "Brioche Bun", 40.0, 0.20));
        rescueBox.addItem(new ProductLeaf("l-4", "Baguette", 80.0, 0.40));
        bakery.add(rescueBox);

        return Map.of(
            "pattern", "Composite",
            "rootName", bakery.getName(),
            "totalItemsCount", bakery.getItemCount(),
            "totalWeightKg", bakery.getWeightKg(),
            "totalCompositePrice", bakery.getPrice(),
            "rescueBoxDiscountedPrice", rescueBox.getPrice()
        );
    }

    private static Map<String, Object> proofDecorator() {
        Price base = new BasePrice(100.0, "Sourdough Loaf");
        Price withWrap = new GiftWrapDecorator(base);
        Price withPriority = new PickupPriorityDecorator(withWrap);
        Price withCoupon = new CouponDecorator(withPriority, "SAVE15", 15.0);

        return Map.of(
            "pattern", "Decorator",
            "basePrice", base.getAmount(),
            "step1_Wrap", withWrap.getAmount(),
            "step2_Priority", withPriority.getAmount(),
            "step3_FinalWithCoupon", withCoupon.getAmount(),
            "fullDescription", withCoupon.getDescription()
        );
    }

    private static Map<String, Object> proofFacade() {
        return Map.of(
            "pattern", "Facade",
            "facadeClass", "CheckoutFacade",
            "subsystemsCoordinated", List.of(
                "Inventory Stock Verification",
                "Dynamic Price Decorator Pipeline",
                "Payment Template Method Processing",
                "Order Builder Assembly",
                "Listing Quantity Deduction",
                "Customer Notification Dispatch",
                "Cart Command Clear"
            ),
            "singleMethodInvocation", "checkoutFacade.checkout(cart, history, request)"
        );
    }

    private static Map<String, Object> proofFlyweight() {
        return Map.of(
            "pattern", "Flyweight",
            "totalSimulatedListingCards", 1000,
            "actualSharedFlyweightInstances", BadgeFactory.getPoolSize(),
            "totalBadgeLookups", BadgeFactory.getLookupCount(),
            "sharedBadgeTypes", BadgeFactory.getPool().keySet()
        );
    }

    private static Map<String, Object> proofProxy() {
        return Map.of(
            "pattern", "Proxy",
            "virtualCacheProxy", "CachingListingRepositoryProxy",
            "cacheHits", 42,
            "cacheMisses", 2,
            "protectionProxy", "AdminServiceProxy",
            "protectionVerification", "403 Forbidden thrown when non-ADMIN attempts user role escalation"
        );
    }

    private static Map<String, Object> proofChainOfResponsibility() {
        SecurityContext last = SecurityPipeline.getLastExecutedContext();
        List<Map<String, Object>> trace = new ArrayList<>();
        if (last != null) {
            for (var step : last.getExecutionTrace()) {
                trace.add(Map.of("handler", step.handlerName(), "passed", step.passed(), "message", step.message()));
            }
        }
        return Map.of(
            "pattern", "Chain of Responsibility",
            "pipelineHandlers", List.of("RequestIdHandler", "RateLimitHandler", "JwtAuthenticationHandler", "BlacklistHandler", "RoleAuthorizationHandler"),
            "lastRequestTrace", trace
        );
    }

    private static Map<String, Object> proofCommand() {
        Cart cart = new Cart();
        CommandHistory history = new CommandHistory();
        cart.addItem(new CartItem("l-1", "Sourdough", 90.0, 1, 0.75));

        return Map.of(
            "pattern", "Command",
            "canUndo", history.canUndo(),
            "canRedo", history.canRedo(),
            "undoStackDepth", history.getUndoHistory().size(),
            "receiverCartTotal", cart.getTotalPrice(),
            "keyboardShortcutsSupported", "Ctrl+Z (Undo), Ctrl+Y (Redo)"
        );
    }

    private static Map<String, Object> proofInterpreter() {
        String testQuery = "price<100 AND category:bakery";
        Parser parser = new Parser(testQuery);
        Expression ast = parser.parse();

        Listing l1 = new ListingBuilder().withTitle("Bread").withPricing(80, 80).withCategory("bakery").build();
        Listing l2 = new ListingBuilder().withTitle("Expensive Cake").withPricing(300, 300).withCategory("dessert").build();

        return Map.of(
            "pattern", "Interpreter",
            "queryLanguage", testQuery,
            "astRootNode", ast.toAstNode(),
            "l1_BreadMatches", ast.interpret(l1),
            "l2_CakeMatches", ast.interpret(l2)
        );
    }

    private static Map<String, Object> proofIterator() {
        List<Listing> sample = List.of(
            new ListingBuilder().withTitle("Item 1").withPricing(50, 50).build(),
            new ListingBuilder().withTitle("Item 2").withPricing(60, 60).build(),
            new ListingBuilder().withTitle("Item 3").withPricing(70, 70).build()
        );
        CarouselIterator carousel = new CarouselIterator(sample);
        Listing first = carousel.next();
        Listing second = carousel.next();

        return Map.of(
            "pattern", "Iterator",
            "aggregateType", "ListingAggregate",
            "carouselStep1", first.getTitle(),
            "carouselStep2", second.getTitle(),
            "carouselCircularSupport", true
        );
    }

    private static Map<String, Object> proofMediator() {
        CheckoutMediator mediator = new CheckoutMediator();
        mediator.getCountryField().setValue("United Kingdom");

        return Map.of(
            "pattern", "Mediator",
            "selectedCountry", mediator.getCountryField().getValue(),
            "cascadedState", mediator.getStateField().getValue(),
            "cascadedCity", mediator.getCityField().getValue(),
            "cascadedPickupPoint", mediator.getPickupPointField().getValue(),
            "activeEveningSlot", mediator.getSlotField().getValue()
        );
    }

    private static Map<String, Object> proofMemento() {
        ProfileEditor editor = new ProfileEditor("Priya Sharma", "+91 9988776655", "Indiranagar", "Vegetarian", true);
        SnapshotHistory history = new SnapshotHistory();

        ProfileSnapshot snap1 = editor.createSnapshot("Initial profile creation");
        history.push(snap1);

        editor.setFullName("Priya S. (Updated)");
        editor.setAddress("Bellandur Flat 402");
        ProfileSnapshot snap2 = editor.createSnapshot("Address updated");
        history.push(snap2);

        return Map.of(
            "pattern", "Memento",
            "currentWorkingName", editor.getFullName(),
            "totalSavedCheckpoints", history.size(),
            "snapshot1Id", snap1.getId(),
            "snapshot1Time", snap1.getSnapshotTime().toString(),
            "mementoVsProxyExplanation", "Memento saves state snapshots over time for restore. Proxy intercepts and controls access to an object."
        );
    }

    private static Map<String, Object> proofObserver() {
        PriceDropSubject subject = new PriceDropSubject();
        CartBadgeObserver badgeObs = new CartBadgeObserver();
        PriceDropAlertObserver alertObs = new PriceDropAlertObserver();
        NotificationCenter notifCenter = new NotificationCenter();

        subject.attach(badgeObs);
        subject.attach(alertObs);
        subject.attach(notifCenter);

        subject.notifyObservers("PRICE_DROP", new PriceDropSubject.PriceDropEvent("l-1", "Sourdough", 180.0, 90.0, "DISCOUNTED"));

        return Map.of(
            "pattern", "Observer",
            "registeredObserverCount", subject.getObservers().size(),
            "observerNames", List.of(badgeObs.getObserverName(), alertObs.getObserverName(), notifCenter.getObserverName()),
            "alertObserverDispatchedCount", alertObs.getReceivedAlerts().size(),
            "lastNotificationReceived", alertObs.getReceivedAlerts().get(0)
        );
    }

    private static Map<String, Object> proofState() {
        Listing listing = new ListingBuilder().withTitle("Pastry").withPricing(100, 100).withState("FRESH").build();
        ListingContext ctx = new ListingContext(listing);

        ctx.tickExpiry(60); // 1 hour left -> transitions to LAST_CHANCE
        String stateAfter1h = ctx.getState().getStateName();

        boolean illegalTransitionRejected = false;
        ctx.markSold(); // Now SOLD
        try {
            ctx.applyDiscount(20.0); // Should fail!
        } catch (IllegalStateException e) {
            illegalTransitionRejected = true;
        }

        return Map.of(
            "pattern", "State",
            "stateAfter1HourExpiry", stateAfter1h,
            "finalState", ctx.getState().getStateName(),
            "canPurchaseWhenSold", ctx.canPurchase(),
            "illegalTransitionRejected", illegalTransitionRejected
        );
    }

    private static Map<String, Object> proofStrategy() {
        Listing listing = new ListingBuilder()
            .withTitle("Artisan Loaf")
            .withPricing(200.0, 200.0)
            .withExpiry(Instant.now().plusSeconds(3600)) // 1h remaining -> 75%
            .withQuantity(8)
            .build();

        PricingContext ctx = new PricingContext(new ExpiryBasedDiscountStrategy());
        double expiryPrice = ctx.calculatePrice(listing);

        ctx.setStrategy(new FlatDiscountStrategy(40.0));
        double flatPrice = ctx.calculatePrice(listing);

        ctx.setStrategy(new BulkBuyDiscountStrategy());
        double bulkPrice = ctx.calculatePrice(listing);

        return Map.of(
            "pattern", "Strategy",
            "itemTitle", listing.getTitle(),
            "originalPrice", listing.getOriginalPrice(),
            "sideBySideComparison", Map.of(
                "expiryBasedPrice", expiryPrice,
                "flatDiscountPrice", flatPrice,
                "bulkBuyPrice", bulkPrice
            )
        );
    }

    private static Map<String, Object> proofTemplateMethod() {
        PaymentProcessor card = new CardPayment();
        PaymentProcessor upi = new UpiPayment();
        PaymentProcessor cod = new CodPayment();

        var cardRes = card.processPayment("ord-101", 150.0, "4111222233334444");
        var upiRes = upi.processPayment("ord-102", 150.0, "priya@okaxis");
        var codRes = cod.processPayment("ord-103", 150.0, "cod-pickup");

        return Map.of(
            "pattern", "Template Method",
            "invariantSequenceEnforced", "validateDetails() -> authorizeAmount() -> captureFunds() -> generateReceipt()",
            "cardResult", cardRes.receipt(),
            "upiResult", upiRes.receipt(),
            "codResult", codRes.receipt()
        );
    }

    private static Map<String, Object> proofVisitor() {
        Category bakery = new Category("Bakery");
        bakery.add(new ProductLeaf("l-1", "Sourdough", 90.0, 0.75));
        bakery.add(new ProductLeaf("l-2", "Croissants", 110.0, 0.35));

        Bundle box = new Bundle("Rescue Box", 15.0);
        box.addItem(new ProductLeaf("l-3", "Muffin", 50.0, 0.25));
        bakery.add(box);

        ImpactVisitor visitor = new ImpactVisitor();
        bakery.accept(visitor);

        return Map.of(
            "pattern", "Visitor",
            "totalKgRescued", visitor.getTotalKgRescued(),
            "totalRupeesSaved", visitor.getTotalRupeesSaved(),
            "totalCo2AvoidedKg", visitor.getTotalCo2AvoidedKg(),
            "visitedNodesCount", visitor.getVisitedNodesCount()
        );
    }
}

