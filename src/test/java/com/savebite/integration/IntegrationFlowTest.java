package com.savebite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savebite.Main;
import com.savebite.patterns.creational.singleton.ConfigManager;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationFlowTest {
    private static Javalin app;
    private static int testPort;
    private static HttpClient client;
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setup() {
        testPort = 7088; // Isolated test port
        app = Main.startServer(testPort);
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void teardown() {
        Main.stopServer();
    }

    @Test
    @DisplayName("End-to-End Integration: Register -> Login -> Search -> Cart -> Checkout -> Logout")
    void testFullCustomerJourney() throws Exception {
        String baseUrl = "http://localhost:" + testPort;

        // Step 1: Register new user
        String regJson = mapper.writeValueAsString(Map.of(
            "email", "integration_user@savebite.org",
            "password", "password123",
            "fullName", "Integration Tester",
            "role", "CUSTOMER",
            "phone", "+919876500000",
            "address", "Bangalore Tech Park"
        ));

        HttpRequest regReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/auth/register"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(regJson))
            .build();

        HttpResponse<String> regRes = client.send(regReq, HttpResponse.BodyHandlers.ofString());
        assertThat(regRes.statusCode()).isIn(201, 400); // 201 created or already exists

        // Step 2: Login to acquire JWT access token
        String loginJson = mapper.writeValueAsString(Map.of(
            "email", "integration_user@savebite.org",
            "password", "password123"
        ));

        HttpRequest loginReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(loginJson))
            .build();

        HttpResponse<String> loginRes = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        assertThat(loginRes.statusCode()).isEqualTo(200);

        Map<?, ?> loginData = mapper.readValue(loginRes.body(), Map.class);
        String token = (String) loginData.get("accessToken");
        assertThat(token).isNotBlank();

        // Step 3: Search listings (evaluated via Interpreter)
        HttpRequest searchReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/listings?q=price%3C200&category=bakery"))
            .GET()
            .build();

        HttpResponse<String> searchRes = client.send(searchReq, HttpResponse.BodyHandlers.ofString());
        assertThat(searchRes.statusCode()).isEqualTo(200);
        Map<?, ?> searchData = mapper.readValue(searchRes.body(), Map.class);
        assertThat(searchData.get("listings")).isNotNull();

        // Step 4: Add item to cart (Command pattern)
        String cartItemJson = mapper.writeValueAsString(Map.of(
            "listingId", "list-001",
            "title", "Artisan Sourdough Loaf",
            "unitPrice", 90.0,
            "quantity", 1,
            "weightKg", 0.75
        ));

        HttpRequest addCartReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/cart/items"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(cartItemJson))
            .build();

        HttpResponse<String> cartRes = client.send(addCartReq, HttpResponse.BodyHandlers.ofString());
        assertThat(cartRes.statusCode()).isEqualTo(200);

        // Step 5: Checkout (Facade, Decorator, Template Method, OrderBuilder)
        String checkoutJson = mapper.writeValueAsString(Map.of(
            "pickupSlot", "18:00 - 19:00",
            "pickupAddress", "Indiranagar Hub",
            "paymentMethod", "UPI",
            "paymentDetails", "integration@okaxis",
            "couponCode", "TEST15",
            "includeGiftWrap", true
        ));

        HttpRequest checkoutReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/checkout"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(checkoutJson))
            .build();

        HttpResponse<String> checkoutRes = client.send(checkoutReq, HttpResponse.BodyHandlers.ofString());
        assertThat(checkoutRes.statusCode()).isEqualTo(200);
        Map<?, ?> checkoutData = mapper.readValue(checkoutRes.body(), Map.class);
        assertThat(checkoutData.get("success")).isEqualTo(true);
        assertThat(checkoutData.get("orderId")).isNotNull();

        // Step 6: Logout and blacklist token
        HttpRequest logoutReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/auth/logout"))
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> logoutRes = client.send(logoutReq, HttpResponse.BodyHandlers.ofString());
        assertThat(logoutRes.statusCode()).isEqualTo(200);

        // Step 7: Verify token is now blacklisted -> 401 Unauthorized
        HttpRequest verifyRevoked = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/orders"))  
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();

        HttpResponse<String> revokedRes = client.send(verifyRevoked, HttpResponse.BodyHandlers.ofString());
        assertThat(revokedRes.statusCode()).isEqualTo(401);
    }
}
