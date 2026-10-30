package com.savebite.patterns.creational.singleton;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

// Pattern: Singleton
// Category: Creational
// Problem it solves here: Logging must have a unified entrypoint with structured formatting and metrics across the entire application without instantiating multiple loggers.
// GoF participant role: Singleton
// Used by: All services, controllers, and security filters
@DesignPattern(name = "Singleton", role = "Singleton", description = "Application-wide structured logger with call metrics")
public class AppLogger {
    private static final Logger SLF4J_LOGGER = LoggerFactory.getLogger("SaveBite");
    private final AtomicInteger logCount = new AtomicInteger(0);

    private AppLogger() {}

    private static class Holder {
        private static final AppLogger INSTANCE = new AppLogger();
    }

    public static AppLogger getInstance() {
        return Holder.INSTANCE;
    }

    public void info(String message, Object... params) {
        logCount.incrementAndGet();
        SLF4J_LOGGER.info(message, params);
        PatternTracer.getInstance().record(
            "Singleton",
            "AppLogger",
            "info",
            "Logged message: " + String.format(message.replace("{}", "%s"), params)
        );
    }

    public void warn(String message, Object... params) {
        logCount.incrementAndGet();
        SLF4J_LOGGER.warn(message, params);
        PatternTracer.getInstance().record(
            "Singleton",
            "AppLogger",
            "warn",
            "Logged warning: " + String.format(message.replace("{}", "%s"), params)
        );
    }

    public void error(String message, Throwable t) {
        logCount.incrementAndGet();
        SLF4J_LOGGER.error(message, t);
        PatternTracer.getInstance().record(
            "Singleton",
            "AppLogger",
            "error",
            "Logged error: " + message + (t != null ? " (" + t.getMessage() + ")" : "")
        );
    }

    public int getLogCount() {
        return logCount.get();
    }

    public String getInstanceId() {
        return "AppLogger@" + Integer.toHexString(System.identityHashCode(this));
    }
}

