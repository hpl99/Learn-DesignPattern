package com.savebite.patterns;

import com.savebite.model.Listing;
import com.savebite.model.Order;
import com.savebite.patterns.behavioral.command.Cart;
import com.savebite.patterns.behavioral.command.CartItem;
import com.savebite.patterns.behavioral.command.CommandHistory;
import com.savebite.patterns.behavioral.templatemethod.PaymentProcessor;
import com.savebite.patterns.behavioral.templatemethod.UpiPayment;
import com.savebite.patterns.creational.builder.ListingBuilder;
import com.savebite.patterns.creational.factorymethod.NotificationFactory;
import com.savebite.patterns.structural.adapter.AuthProvider;
import com.savebite.patterns.structural.adapter.AuthResult;
import com.savebite.patterns.structural.adapter.GithubAuthAdapter;
import com.savebite.patterns.structural.adapter.GoogleAuthAdapter;
import com.savebite.patterns.structural.bridge.*;
import com.savebite.patterns.structural.composite.Bundle;
import com.savebite.patterns.structural.composite.Category;
import com.savebite.patterns.structural.composite.ProductLeaf;
import com.savebite.patterns.structural.decorator.*;
import com.savebite.patterns.structural.facade.CheckoutFacade;
import com.savebite.patterns.structural.flyweight.BadgeFactory;
import com.savebite.patterns.structural.flyweight.BadgeFlyweight;
import com.savebite.patterns.structural.proxy.AdminService;
import com.savebite.patterns.structural.proxy.AdminServiceProxy;
import com.savebite.patterns.structural.proxy.CachingListingRepositoryProxy;
import com.savebite.patterns.structural.proxy.RealAdminService;
import com.savebite.repository.ListingRepository;
import com.savebite.repository.OrderRepository;
import com.savebite.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StructuralPatternsTest {

    // ==========================================
    // 6. ADAPTER TEST
    // ==========================================
    @Test
    @DisplayName("Adapter: adapts disparate Google and GitHub APIs into standard AuthProvider")
    void testAdapter() {
        AuthProvider google = new GoogleAuthAdapter();
        AuthResult res1 = google.authenticate("google-token-alice");
        assertThat(res1.getProvider()).isEqualTo("google");
        assertThat(res1.getEmail()).contains("@gmail.com");

        AuthProvider github = new GithubAuthAdapter();
        AuthResult res2 = github.authenticate("gh-code-bob");
        assertThat(res2.getProvider()).isEqualTo("github");
        assertThat(res2.getEmail()).contains("@github.user");
    }

    // ==========================================
    // 7. BRIDGE TEST
    // ==========================================
    @Test
    @DisplayName("Bridge: report abstraction dynamically switches between CSV, HTML, and PDF exporters")
    void testBridge() {
        SalesReport report = new SalesReport(new CsvExporter(), "Artisan Oven");
        report.addSale("Sourdough", "10", "900");

        String csv = report.generate();
        assertThat(csv).contains("# Sales Summary").contains("Sourdough,10,₹900");

        report.setExporter(new HtmlExporter());
        String html = report.generate();
        assertThat(html).contains("<table").contains("<td>Sourdough</td>");

        report.setExporter(new PdfExporter());
        String pdf = report.generate();
        assertThat(pdf).contains("SAVEBITE OFFICIAL REPORT").contains("Sourdough");
    }

    // ==========================================
    // 8. COMPOSITE TEST
    // ==========================================
    @Test
    @DisplayName("Composite: uniform calculation across leaves, rescue bundles, and categories")
    void testComposite() {
        Category bakery = new Category("Bakery");
        ProductLeaf item1 = new ProductLeaf("l1", "Croissant", 40.0, 0.1);
        ProductLeaf item2 = new ProductLeaf("l2", "Baguette", 60.0, 0.3);
        bakery.add(item1);
        bakery.add(item2);

        Bundle rescueBox = new Bundle("Evening Mystery Box", 20.0); // 20% discount
        rescueBox.addItem(new ProductLeaf("l3", "Bun", 50.0, 0.2));
        rescueBox.addItem(new ProductLeaf("l4", "Muffin", 50.0, 0.2));
        bakery.add(rescueBox);

        // Bundle: (50+50) * 0.8 = 80.0
        assertThat(rescueBox.getPrice()).isEqualTo(80.0);
        assertThat(rescueBox.getItemCount()).isEqualTo(2);

        // Category total: 40 + 60 + 80 = 180.0
        assertThat(bakery.getPrice()).isEqualTo(180.0);
        assertThat(bakery.getItemCount()).isEqualTo(4);
        assertThat(bakery.getWeightKg()).isEqualTo(0.8);
    }

    // ==========================================
    // 9. DECORATOR TEST
    // ==========================================
    @Test
    @DisplayName("Decorator: dynamically stacks packaging, priority slot, and promo coupons")
    void testDecorator() {
        Price price = new BasePrice(100.0, "Food Base");
        assertThat(price.getAmount()).isEqualTo(100.0);

        price = new GiftWrapDecorator(price); // +25
        assertThat(price.getAmount()).isEqualTo(125.0);

        price = new PickupPriorityDecorator(price); // +15
        assertThat(price.getAmount()).isEqualTo(140.0);

        price = new CouponDecorator(price, "SAVE10", 10.0); // 10% off 140 -> 126
        assertThat(price.getAmount()).isEqualTo(126.0);
        assertThat(price.getDescription()).contains("Food Base").contains("Thermal Eco-Packaging").contains("Coupon SAVE10");
    }

    // ==========================================
    // 10. FACADE TEST
    // ==========================================
    @Test
    @DisplayName("Facade: CheckoutFacade coordinates inventory, decorators, payment, order builder, and cart clear")
    void testFacade() {
        ListingRepository listingRepo = mock(ListingRepository.class);
        OrderRepository orderRepo = mock(OrderRepository.class);
        Listing sample = new ListingBuilder().withId("l1").withTitle("Sourdough").withPricing(100, 50).withQuantity(5).build();

        when(listingRepo.findById("l1")).thenReturn(Optional.of(sample));

        Map<String, PaymentProcessor> processors = Map.of("UPI", new UpiPayment());
        CheckoutFacade facade = new CheckoutFacade(listingRepo, orderRepo, processors, new NotificationFactory());

        Cart cart = new Cart();
        cart.addItem(new CartItem("l1", "Sourdough", 50.0, 2, 0.5));
        CommandHistory history = new CommandHistory();

        CheckoutFacade.CheckoutRequest req = new CheckoutFacade.CheckoutRequest(
            "cust1", "cust@savebite.org", "18:00 - 19:00", "Hub Indiranagar",
            "UPI", "priya@okaxis", "PROMO15", true, false
        );

        CheckoutFacade.CheckoutResponse resp = facade.checkout(cart, history, req);
        assertThat(resp.success()).isTrue();
        assertThat(resp.orderId()).isNotNull();
        assertThat(cart.getItems()).isEmpty(); // Cart cleared!
        verify(orderRepo, times(1)).save(any(Order.class));
    }

    // ==========================================
    // 11. FLYWEIGHT TEST
    // ==========================================
    @Test
    @DisplayName("Flyweight: pools intrinsic badge state across arbitrary lookups")
    void testFlyweight() {
        BadgeFlyweight fresh1 = BadgeFactory.getBadge("FRESH");
        BadgeFlyweight fresh2 = BadgeFactory.getBadge("FRESH");

        // Exact same object reference in memory!
        assertThat(fresh1).isSameAs(fresh2);
        assertThat(fresh1.getLabel()).isEqualTo("Fresh Arrival");

        String rendered = fresh1.render("listing-42", 1);
        assertThat(rendered).contains("Fresh Arrival").contains("listing-42");
    }

    // ==========================================
    // 12. PROXY TEST
    // ==========================================
    @Test
    @DisplayName("Proxy: Caching proxy hits cache and Protection proxy denies non-admin roles")
    void testProxy() {
        // Caching virtual proxy
        ListingRepository mockRepo = mock(ListingRepository.class);
        Listing listing = new ListingBuilder().withId("l1").withTitle("Croissant").withPricing(50, 25).build();
        when(mockRepo.findById("l1")).thenReturn(Optional.of(listing));

        CachingListingRepositoryProxy cacheProxy = new CachingListingRepositoryProxy(mockRepo);
        // Call 1: MISS
        cacheProxy.findById("l1");
        verify(mockRepo, times(1)).findById("l1");

        // Call 2: HIT
        cacheProxy.findById("l1");
        verify(mockRepo, times(1)).findById("l1"); // No second DB call!
        assertThat(cacheProxy.getCacheHits()).isEqualTo(1);

        // Protection proxy
        AdminService realAdmin = mock(AdminService.class);
        AdminServiceProxy adminProxy = new AdminServiceProxy(realAdmin);

        // Non-admin blocked with SecurityException
        assertThatThrownBy(() -> adminProxy.deleteListing("CUSTOMER", "l1"))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("Access Denied: Only ADMIN role");

        verify(realAdmin, never()).deleteListing(any(), any());

        // Admin allowed
        adminProxy.deleteListing("ADMIN", "l1");
        verify(realAdmin, times(1)).deleteListing("ADMIN", "l1");
    }
}

