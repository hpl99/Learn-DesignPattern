package com.savebite.patterns.creational.singleton;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

// Pattern: Singleton
// Category: Creational
// Problem it solves here: Application configuration must be centralized, read once from environment/defaults, and consistently shared across all server components.
// GoF participant role: Singleton
// Used by: Server, Database, JwtService, Repositories
@DesignPattern(name = "Singleton", role = "Singleton", description = "Thread-safe global application configuration manager")
public class ConfigManager {
    private final Properties properties = new Properties();
    private final AtomicInteger accessCount = new AtomicInteger(0);
    private final long createdAtEpoch;

    private ConfigManager() {
        this.createdAtEpoch = System.currentTimeMillis();
        loadDefaults();
        PatternTracer.getInstance().record(
            "Singleton",
            "ConfigManager",
            "<init>",
            "ConfigManager instance initialized successfully with default properties."
        );
    }

    private static class InstanceHolder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        ConfigManager instance = InstanceHolder.INSTANCE;
        instance.accessCount.incrementAndGet();
        PatternTracer.getInstance().record(
            "Singleton",
            "ConfigManager",
            "getInstance",
            "Accessed ConfigManager singleton (access count: " + instance.accessCount.get() + ")"
        );
        return instance;
    }

    private void loadDefaults() {
        properties.setProperty("server.port", System.getenv().getOrDefault("PORT", "7070"));
        properties.setProperty("db.url", System.getenv().getOrDefault("DB_URL", "jdbc:h2:./data/savebite;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE"));
        properties.setProperty("db.user", System.getenv().getOrDefault("DB_USER", "sa"));
        properties.setProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", ""));
        properties.setProperty("jwt.secret", System.getenv().getOrDefault("JWT_SECRET", "SaveBiteSuperSecretKeyForHS256MustBeAtLeast32BytesLong123!"));
        properties.setProperty("jwt.access.expiry.minutes", "15");
        properties.setProperty("jwt.refresh.expiry.days", "7");
        properties.setProperty("app.teaching.mode", "true");
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String val = properties.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int getAccessCount() {
        return accessCount.get();
    }

    public long getCreatedAtEpoch() {
        return createdAtEpoch;
    }

    public String getInstanceId() {
        return "ConfigManager@" + Integer.toHexString(System.identityHashCode(this));
    }
}

