package com.savebite.repository.jdbc;

import com.savebite.patterns.creational.singleton.AppLogger;
import com.savebite.patterns.creational.singleton.ConfigManager;
import com.savebite.teaching.PatternTracer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final DatabaseManager INSTANCE = new DatabaseManager();
    private final String url;
    private final String user;
    private final String password;

    private DatabaseManager() {
        this.url = ConfigManager.getInstance().get("db.url");
        this.user = ConfigManager.getInstance().get("db.user");
        this.password = ConfigManager.getInstance().get("db.password");
        initSchema();
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void initSchema() {
        String schema = """
            CREATE TABLE IF NOT EXISTS users (
                id VARCHAR(64) PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                full_name VARCHAR(255) NOT NULL,
                role VARCHAR(32) NOT NULL,
                phone VARCHAR(64),
                address VARCHAR(512),
                created_at TIMESTAMP NOT NULL
            );

            CREATE TABLE IF NOT EXISTS listings (
                id VARCHAR(64) PRIMARY KEY,
                seller_id VARCHAR(64) NOT NULL,
                seller_name VARCHAR(255) NOT NULL,
                title VARCHAR(255) NOT NULL,
                description CLOB,
                category VARCHAR(64) NOT NULL,
                original_price DOUBLE NOT NULL,
                current_price DOUBLE NOT NULL,
                quantity INT NOT NULL,
                expiry_time TIMESTAMP NOT NULL,
                state VARCHAR(32) NOT NULL,
                photo_url VARCHAR(512),
                weight_kg DOUBLE NOT NULL,
                created_at TIMESTAMP NOT NULL
            );

            CREATE TABLE IF NOT EXISTS orders (
                id VARCHAR(64) PRIMARY KEY,
                customer_id VARCHAR(64) NOT NULL,
                customer_email VARCHAR(255) NOT NULL,
                total_amount DOUBLE NOT NULL,
                status VARCHAR(32) NOT NULL,
                pickup_slot VARCHAR(128) NOT NULL,
                pickup_address VARCHAR(512) NOT NULL,
                payment_method VARCHAR(64) NOT NULL,
                payment_status VARCHAR(64) NOT NULL,
                created_at TIMESTAMP NOT NULL
            );

            CREATE TABLE IF NOT EXISTS order_items (
                id VARCHAR(64) PRIMARY KEY,
                order_id VARCHAR(64) NOT NULL,
                listing_id VARCHAR(64) NOT NULL,
                title VARCHAR(255) NOT NULL,
                quantity INT NOT NULL,
                unit_price DOUBLE NOT NULL,
                subtotal DOUBLE NOT NULL,
                weight_kg DOUBLE NOT NULL
            );

            CREATE TABLE IF NOT EXISTS audit_logs (
                id VARCHAR(64) PRIMARY KEY,
                user_email VARCHAR(255) NOT NULL,
                action VARCHAR(128) NOT NULL,
                details CLOB,
                timestamp TIMESTAMP NOT NULL
            );

            CREATE TABLE IF NOT EXISTS token_blacklist (
                jti VARCHAR(64) PRIMARY KEY,
                revoked_at TIMESTAMP NOT NULL,
                expires_at TIMESTAMP NOT NULL
            );
            """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(schema);
            AppLogger.getInstance().info("H2 database schema initialized successfully at {}", url);
            PatternTracer.getInstance().record(
                "Singleton",
                "DatabaseManager",
                "initSchema",
                "Database schema created/verified in H2 embedded storage"
            );
        } catch (SQLException e) {
            AppLogger.getInstance().error("Failed to initialize H2 database schema", e);
            throw new RuntimeException("Database init failed", e);
        }
    }
}

