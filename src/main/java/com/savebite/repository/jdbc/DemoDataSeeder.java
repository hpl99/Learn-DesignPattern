package com.savebite.repository.jdbc;

import com.savebite.model.Listing;
import com.savebite.model.Role;
import com.savebite.model.User;
import com.savebite.patterns.creational.builder.ListingBuilder;
import com.savebite.patterns.creational.singleton.AppLogger;
import com.savebite.repository.ListingRepository;
import com.savebite.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class DemoDataSeeder {

    public static void seedIfEmpty(UserRepository userRepo, ListingRepository listingRepo) {
        if (!userRepo.findByEmail("admin@savebite.org").isPresent()) {
            AppLogger.getInstance().info("Seeding initial demo data for SaveBite...");

            // 1. Seed Users
            String adminHash = BCrypt.hashpw("admin123", BCrypt.gensalt(10));
            String sellerHash = BCrypt.hashpw("baker123", BCrypt.gensalt(10));
            String customerHash = BCrypt.hashpw("customer123", BCrypt.gensalt(10));

            User admin = new User(
                "usr-admin-01",
                "admin@savebite.org",
                adminHash,
                "Aarav Patel (Platform Admin)",
                Role.ADMIN,
                "+91 98765 43210",
                "SaveBite Operations HQ, Bangalore",
                Instant.now()
            );
            userRepo.save(admin);

            User seller = new User(
                "usr-seller-01",
                "baker@savebite.org",
                sellerHash,
                "Artisan Oven Bakery & Café",
                Role.SELLER,
                "+91 91234 56789",
                "Shop 14, Indiranagar 100ft Road, Bangalore",
                Instant.now()
            );
            userRepo.save(seller);

            User customer = new User(
                "usr-cust-01",
                "customer@savebite.org",
                customerHash,
                "Priya Sharma",
                Role.CUSTOMER,
                "+91 99887 76655",
                "Flat 402, Green Glen Layout, Bellandur, Bangalore",
                Instant.now()
            );
            userRepo.save(customer);

            // 2. Seed Realistic Surplus Listings
            Instant now = Instant.now();

            Listing l1 = new ListingBuilder()
                .withId("list-001")
                .withSeller(seller.getId(), seller.getFullName())
                .withTitle("Artisan Sourdough Loaf (Whole Grain)")
                .withDescription("Freshly baked at 6:00 AM, golden crust with moist crumb. Perfect for bruschetta or toasts.")
                .withCategory("bakery")
                .withPricing(180.0, 90.0) // 50% off
                .withQuantity(8)
                .withWeightKg(0.75)
                .withState("DISCOUNTED")
                .withPhotoUrl("https://images.unsplash.com/photo-1589367920969-ab8e050bbb04?w=400&q=80")
                .withExpiry(now.plus(3, ChronoUnit.HOURS))
                .build();
            listingRepo.save(l1);

            Listing l2 = new ListingBuilder()
                .withId("list-002")
                .withSeller(seller.getId(), seller.getFullName())
                .withTitle("Butter Flaky Croissants (Pack of 3)")
                .withDescription("French laminated pastry made with pure New Zealand butter. Crisp exterior with honeycomb interior.")
                .withCategory("bakery")
                .withPricing(240.0, 110.0)
                .withQuantity(5)
                .withWeightKg(0.35)
                .withState("DISCOUNTED")
                .withPhotoUrl("https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400&q=80")
                .withExpiry(now.plus(2, ChronoUnit.HOURS))
                .build();
            listingRepo.save(l2);

            Listing l3 = new ListingBuilder()
                .withId("list-003")
                .withSeller(seller.getId(), seller.getFullName())
                .withTitle("Organic Greek Yogurt & Berry Parfait")
                .withDescription("High-protein strained yogurt layered with wild blueberry compote and toasted rolled oats.")
                .withCategory("dairy")
                .withPricing(150.0, 60.0)
                .withQuantity(12)
                .withWeightKg(0.30)
                .withState("LAST_CHANCE")
                .withPhotoUrl("https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400&q=80")
                .withExpiry(now.plus(1, ChronoUnit.HOURS))
                .build();
            listingRepo.save(l3);

            Listing l4 = new ListingBuilder()
                .withId("list-004")
                .withSeller(seller.getId(), seller.getFullName())
                .withTitle("Chef's Gourmet Veggie Lasagna Box")
                .withDescription("Hearty oven-baked layers of pasta sheets, zucchini, spinach, ricotta cheese, and basil marinara.")
                .withCategory("prepared")
                .withPricing(320.0, 140.0)
                .withQuantity(6)
                .withWeightKg(0.65)
                .withState("DISCOUNTED")
                .withPhotoUrl("https://images.unsplash.com/photo-1574894709920-11b28e7367e3?w=400&q=80")
                .withExpiry(now.plus(4, ChronoUnit.HOURS))
                .build();
            listingRepo.save(l4);

            Listing l5 = new ListingBuilder()
                .withId("list-005")
                .withSeller(seller.getId(), seller.getFullName())
                .withTitle("Cold Pressed Valencia Orange Juice (500ml)")
                .withDescription("100% pure raw juice squeezed this morning. No added sugar, water, or preservatives.")
                .withCategory("beverages")
                .withPricing(120.0, 50.0)
                .withQuantity(10)
                .withWeightKg(0.50)
                .withState("LAST_CHANCE")
                .withPhotoUrl("https://images.unsplash.com/photo-1613478223719-2ab802602423?w=400&q=80")
                .withExpiry(now.plus(1, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES))
                .build();
            listingRepo.save(l5);

            Listing l6 = new ListingBuilder()
                .withId("list-006")
                .withSeller(seller.getId(), seller.getFullName())
                .withTitle("Dark Chocolate Truffle Tart (Slice)")
                .withDescription("70% Belgian chocolate ganache on a buttery sablé crust with sea salt flakes.")
                .withCategory("dessert")
                .withPricing(190.0, 95.0)
                .withQuantity(4)
                .withWeightKg(0.20)
                .withState("FRESH")
                .withPhotoUrl("https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&q=80")
                .withExpiry(now.plus(6, ChronoUnit.HOURS))
                .build();
            listingRepo.save(l6);

            AppLogger.getInstance().info("Demo data seeding completed: 3 users, 6 listings.");
        }
    }
}

