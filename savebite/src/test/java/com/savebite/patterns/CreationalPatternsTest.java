package com.savebite.patterns;

import com.savebite.model.Listing;
import com.savebite.model.Order;
import com.savebite.model.OrderItem;
import com.savebite.patterns.creational.abstractfactory.DarkThemeFactory;
import com.savebite.patterns.creational.abstractfactory.LightThemeFactory;
import com.savebite.patterns.creational.builder.ListingBuilder;
import com.savebite.patterns.creational.builder.OrderBuilder;
import com.savebite.patterns.creational.factorymethod.Notification;
import com.savebite.patterns.creational.factorymethod.NotificationChannel;
import com.savebite.patterns.creational.factorymethod.NotificationFactory;
import com.savebite.patterns.creational.factorymethod.ToastNotification;
import com.savebite.patterns.creational.prototype.ListingPrototype;
import com.savebite.patterns.creational.prototype.OrderPrototype;
import com.savebite.patterns.creational.prototype.ReorderService;
import com.savebite.patterns.creational.singleton.AppLogger;
import com.savebite.patterns.creational.singleton.ConfigManager;
import com.savebite.patterns.creational.singleton.JwtKeyHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CreationalPatternsTest {

    // ==========================================
    // 1. SINGLETON TEST
    // ==========================================
    @Test
    @DisplayName("Singleton: should return exact same instance, also under concurrent threads")
    void testSingletonThreadSafety() throws InterruptedException, ExecutionException {
        ConfigManager instance1 = ConfigManager.getInstance();
        ConfigManager instance2 = ConfigManager.getInstance();
        assertThat(instance1).isSameAs(instance2);

        AppLogger logger1 = AppLogger.getInstance();
        AppLogger logger2 = AppLogger.getInstance();
        assertThat(logger1).isSameAs(logger2);

        JwtKeyHolder key1 = JwtKeyHolder.getInstance();
        JwtKeyHolder key2 = JwtKeyHolder.getInstance();
        assertThat(key1).isSameAs(key2);
        assertThat(key1.getSecretKey()).isNotNull();

        // Concurrent multi-thread access
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<ConfigManager>> tasks = Collections.nCopies(threads, ConfigManager::getInstance);
        List<Future<ConfigManager>> futures = executor.invokeAll(tasks);

        for (Future<ConfigManager> future : futures) {
            assertThat(future.get()).isSameAs(instance1);
        }
        executor.shutdown();
    }

    // ==========================================
    // 2. FACTORY METHOD TEST
    // ==========================================
    @Test
    @DisplayName("Factory Method: should instantiate correct notification subtypes per channel")
    void testFactoryMethod() {
        NotificationFactory factory = new NotificationFactory();

        Notification toast = factory.createNotification(NotificationChannel.TOAST);
        assertThat(toast).isInstanceOf(ToastNotification.class);
        assertThat(toast.getChannel()).isEqualTo(NotificationChannel.TOAST);
        assertThat(toast.getFormattedPayload("user@test.com", "Test alert")).contains("[TOAST UI]");

        Notification email = factory.createNotification(NotificationChannel.EMAIL);
        assertThat(email.getFormattedPayload("user@test.com", "Receipt")).contains("[EMAIL SMTP]");

        Notification sms = factory.createNotification(NotificationChannel.SMS);
        assertThat(sms.getFormattedPayload("+919876543210", "Code")).contains("[SMS GATEWAY]");
    }

    // ==========================================
    // 3. ABSTRACT FACTORY TEST
    // ==========================================
    @Test
    @DisplayName("Abstract Factory: should produce harmonious component families for light and dark themes")
    void testAbstractFactory() {
        var lightFactory = new LightThemeFactory();
        assertThat(lightFactory.getThemeName()).isEqualTo("light");
        assertThat(lightFactory.createButton().getBackgroundColor()).isEqualTo("#16a34a");
        assertThat(lightFactory.createCard().getBackgroundColor()).isEqualTo("#ffffff");

        var darkFactory = new DarkThemeFactory();
        assertThat(darkFactory.getThemeName()).isEqualTo("dark");
        assertThat(darkFactory.createButton().getBackgroundColor()).isEqualTo("#22c55e");
        assertThat(darkFactory.createCard().getBackgroundColor()).isEqualTo("#1e293b");
    }

    // ==========================================
    // 4. BUILDER TEST
    // ==========================================
    @Test
    @DisplayName("Builder: step-by-step construction and mandatory field validation")
    void testBuilder() {
        Listing listing = new ListingBuilder()
            .withTitle("Artisan Focaccia")
            .withPricing(160.0, 80.0)
            .withCategory("bakery")
            .withWeightKg(0.5)
            .build();

        assertThat(listing.getTitle()).isEqualTo("Artisan Focaccia");
        assertThat(listing.getOriginalPrice()).isEqualTo(160.0);
        assertThat(listing.getCurrentPrice()).isEqualTo(80.0);
        assertThat(listing.getState()).isEqualTo("FRESH");

        // Missing title must throw IllegalStateException
        assertThatThrownBy(() -> new ListingBuilder().withPricing(100, 50).build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Listing title is required");

        // OrderBuilder with items
        OrderItem item = new OrderItem("i1", "ord1", "l1", "Focaccia", 1, 80.0, 80.0, 0.5);
        Order order = new OrderBuilder()
            .withCustomer("cust-1", "test@test.com")
            .addItem(item)
            .build();

        assertThat(order.getCustomerId()).isEqualTo("cust-1");
        assertThat(order.getTotalAmount()).isEqualTo(80.0);
    }

    // ==========================================
    // 5. PROTOTYPE TEST
    // ==========================================
    @Test
    @DisplayName("Prototype: deep cloning past orders and listing templates with distinct identities")
    void testPrototype() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("item-1", "order-1", "list-1", "Sourdough", 2, 90.0, 180.0, 0.75));

        Order original = new Order(
            "order-1", "cust-1", "sharma@test.com", items, 180.0,
            "COMPLETED", "18:00 - 19:00", "Hub 1", "UPI", "PAID", Instant.now().minusSeconds(86400)
        );

        ReorderService reorderService = new ReorderService();
        Order clone = reorderService.reorder(original);

        // Identities must differ
        assertThat(clone.getId()).isNotEqualTo(original.getId());
        assertThat(clone.getStatus()).isEqualTo("CREATED");
        assertThat(clone.getPaymentStatus()).isEqualTo("PENDING");
        assertThat(clone.getItems()).hasSize(1);
        assertThat(clone.getItems().get(0).getId()).isNotEqualTo(original.getItems().get(0).getId());
        assertThat(clone.getItems().get(0).getOrderId()).isEqualTo(clone.getId());

        // ListingPrototype test
        Listing template = new ListingBuilder().withTitle("Daily Baguette").withPricing(100, 50).build();
        ListingPrototype proto = new ListingPrototype(template);
        Listing clonedListing = proto.copy();

        assertThat(clonedListing.getId()).isNotEqualTo(template.getId());
        assertThat(clonedListing.getTitle()).contains("Daily Baguette");
    }
}

