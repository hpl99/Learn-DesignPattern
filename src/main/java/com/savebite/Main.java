package com.savebite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.savebite.controller.*;
import com.savebite.patterns.behavioral.chainofresponsibility.SecurityPipeline;
import com.savebite.patterns.behavioral.observer.CartBadgeObserver;
import com.savebite.patterns.behavioral.observer.NotificationCenter;
import com.savebite.patterns.behavioral.observer.PriceDropAlertObserver;
import com.savebite.patterns.behavioral.observer.PriceDropSubject;
import com.savebite.patterns.behavioral.templatemethod.CardPayment;
import com.savebite.patterns.behavioral.templatemethod.CodPayment;
import com.savebite.patterns.behavioral.templatemethod.PaymentProcessor;
import com.savebite.patterns.behavioral.templatemethod.UpiPayment;
import com.savebite.patterns.creational.factorymethod.NotificationFactory;
import com.savebite.patterns.creational.singleton.AppLogger;
import com.savebite.patterns.creational.singleton.ConfigManager;
import com.savebite.patterns.structural.adapter.AuthProvider;
import com.savebite.patterns.structural.adapter.GithubAuthAdapter;
import com.savebite.patterns.structural.adapter.GoogleAuthAdapter;
import com.savebite.patterns.structural.facade.CheckoutFacade;
import com.savebite.patterns.structural.facade.LoginFacade;
import com.savebite.patterns.structural.proxy.AdminService;
import com.savebite.patterns.structural.proxy.AdminServiceProxy;
import com.savebite.patterns.structural.proxy.CachingListingRepositoryProxy;
import com.savebite.patterns.structural.proxy.RealAdminService;
import com.savebite.repository.AuditRepository;
import com.savebite.repository.ListingRepository;
import com.savebite.repository.OrderRepository;
import com.savebite.repository.UserRepository;
import com.savebite.repository.jdbc.*;
import com.savebite.security.JwtService;
import com.savebite.security.SecurityFilter;
import com.savebite.service.AuthService;
import com.savebite.service.ImpactService;
import com.savebite.service.ListingService;
import com.savebite.service.OrderService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Javalin app;
    private static ScheduledExecutorService scheduler;

    public static void main(String[] args) {
        startServer(ConfigManager.getInstance().getInt("server.port", 7070));
    }

    public static Javalin startServer(int port) {
        AppLogger.getInstance().info("Starting SaveBite on port {}...", port);

        // 1. Repositories
        UserRepository userRepo = new JdbcUserRepository();
        ListingRepository realListingRepo = new JdbcListingRepository();
        ListingRepository listingRepo = new CachingListingRepositoryProxy(realListingRepo);
        OrderRepository orderRepo = new JdbcOrderRepository();
        AuditRepository auditRepo = new JdbcAuditRepository();

        // Seed initial realistic demo data
        DemoDataSeeder.seedIfEmpty(userRepo, listingRepo);

        // 2. Security & Observers
        JwtService jwtService = new JwtService();
        PriceDropSubject priceDropSubject = new PriceDropSubject();
        CartBadgeObserver cartBadgeObserver = new CartBadgeObserver();
        PriceDropAlertObserver priceDropAlertObserver = new PriceDropAlertObserver();
        NotificationCenter notificationCenter = new NotificationCenter();

        priceDropSubject.attach(cartBadgeObserver);
        priceDropSubject.attach(priceDropAlertObserver);
        priceDropSubject.attach(notificationCenter);

        // 3. Adapters & Payment Processors
        Map<String, AuthProvider> authProviders = Map.of(
            "google", new GoogleAuthAdapter(),
            "github", new GithubAuthAdapter()
        );

        Map<String, PaymentProcessor> paymentProcessors = Map.of(
            "CARD", new CardPayment(),
            "UPI", new UpiPayment(),
            "COD", new CodPayment()
        );

        NotificationFactory notificationFactory = new NotificationFactory();

        // 4. Facades & Proxies
        LoginFacade loginFacade = new LoginFacade(userRepo, auditRepo, authProviders);
        CheckoutFacade checkoutFacade = new CheckoutFacade(listingRepo, orderRepo, paymentProcessors, notificationFactory);

        AdminService realAdminService = new RealAdminService(userRepo, listingRepo, auditRepo);
        AdminService adminServiceProxy = new AdminServiceProxy(realAdminService);

        // 5. Services
        AuthService authService = new AuthService(userRepo, loginFacade, jwtService);
        ListingService listingService = new ListingService(listingRepo, priceDropSubject);
        OrderService orderService = new OrderService(orderRepo);
        ImpactService impactService = new ImpactService(listingRepo, orderRepo);

        // 6. Controllers
        AuthController authController = new AuthController(authService);
        ListingController listingController = new ListingController(listingService);
        CartController cartController = new CartController(cartBadgeObserver);
        CheckoutController checkoutController = new CheckoutController(checkoutFacade, cartController);
        OrderController orderController = new OrderController(orderService);
        SellerController sellerController = new SellerController(listingService, orderService);
        AdminController adminController = new AdminController(adminServiceProxy, auditRepo, impactService);
        ThemeController themeController = new ThemeController();
        ProfileController profileController = new ProfileController();
        TeachingController teachingController = new TeachingController();

        // 7. Security Pipeline
        SecurityPipeline securityPipeline = new SecurityPipeline();

        // 8. Jackson ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 9. Javalin Configuration
        app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper, false));
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.anyHost()));
        });

        // Register Global Security Filter (Chain of Responsibility)
        app.before(new SecurityFilter(securityPipeline));

        // Global Exception Handler
        app.exception(Exception.class, (e, ctx) -> {
            AppLogger.getInstance().error("Unhandled server exception: " + e.getMessage(), e);
            int status = (e instanceof IllegalArgumentException) ? 400 :
                         (e instanceof SecurityException) ? 403 : 500;
            ctx.status(status).json(Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "error", status == 400 ? "Bad Request" : (status == 403 ? "Forbidden" : "Internal Server Error"),
                "message", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred",
                "path", ctx.path()
            ));
        });

        // 10. API Route Mapping
        // Auth
        app.post("/api/auth/login", authController::login);
        app.post("/api/auth/oauth", authController::loginOAuth);
        app.post("/api/auth/register", authController::register);
        app.post("/api/auth/refresh", authController::refresh);
        app.post("/api/auth/logout", authController::logout);
        app.get("/api/auth/me", authController::me);

        // Listings & Catalog
        app.get("/api/listings", listingController::searchListings);
        app.get("/api/listings/{id}", listingController::getListingById);
        app.post("/api/listings/{id}/clone", listingController::cloneListingTemplate);

        // Cart
        app.get("/api/cart", cartController::getCart);
        app.post("/api/cart/items", cartController::addItem);
        app.delete("/api/cart/items/{id}", cartController::removeItem);
        app.post("/api/cart/undo", cartController::undo);
        app.post("/api/cart/redo", cartController::redo);
        app.delete("/api/cart", cartController::clear);

        // Checkout
        app.get("/api/checkout/form", checkoutController::getFormState);
        app.post("/api/checkout/field", checkoutController::updateFormField);
        app.post("/api/checkout", checkoutController::processCheckout);

        // Orders
        app.get("/api/orders", orderController::getMyOrders);
        app.get("/api/orders/{id}", orderController::getOrderById);
        app.post("/api/orders/{id}/reorder", orderController::reorder);
        app.patch("/api/orders/{id}/status", orderController::updateStatus);

        // Seller
        app.get("/api/seller/listings", sellerController::getSellerListings);
        app.post("/api/seller/listings", listingController::createListing);
        app.get("/api/seller/reports/sales", sellerController::exportSalesReport);
        app.post("/api/seller/orders/{id}/pickup", sellerController::markOrderPickedUp);

        // Admin
        app.get("/api/admin/users", adminController::listUsers);
        app.put("/api/admin/users/{id}/role", adminController::updateUserRole);
        app.delete("/api/admin/listings/{id}", adminController::deleteListing);
        app.get("/api/admin/audit", adminController::getAuditLogs);
        app.get("/api/admin/impact", adminController::exportImpactReport);

        // Public Impact Dashboard
        app.get("/api/impact", adminController::getGlobalImpact);

        // Theme Tokens (Abstract Factory)
        app.get("/api/theme/tokens", themeController::getThemeTokens);

        // Profile (Memento)
        app.get("/api/profile", profileController::getProfile);
        app.put("/api/profile", profileController::updateProfile);
        app.post("/api/profile/snapshots", profileController::createSnapshot);
        app.get("/api/profile/snapshots", profileController::listSnapshots);
        app.post("/api/profile/snapshots/{id}/restore", profileController::restoreSnapshot);

        // Teaching & GoF Pattern Explorer
        app.sse("/api/teaching/stream", teachingController::streamEvents);
        app.get("/api/patterns", teachingController::getAllPatterns);
        app.get("/api/patterns/{id}", teachingController::getPattern);
        app.get("/api/patterns/{id}/code", teachingController::getPatternCode);
        app.get("/api/patterns/{id}/proof", teachingController::getPatternProof);
        app.get("/api/teaching/trace", teachingController::getTraceHistory);
        app.get("/api/teaching/trace/export", teachingController::exportTraceText);
        app.post("/api/teaching/trace/clear", teachingController::clearTrace);

        // Notifications
        app.get("/api/notifications", ctx -> ctx.json(notificationCenter.getItems()));

        // Start scheduler for near-expiry automated markdown
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(listingService::repriceNearExpiryListings, 1, 1, TimeUnit.MINUTES);

        app.start(port);
        AppLogger.getInstance().info("SaveBite running at http://localhost:{}", port);

        return app;
    }

    public static void stopServer() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        if (app != null) {
            app.stop();
        }
    }
}
