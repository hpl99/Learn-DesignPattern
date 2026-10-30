package com.savebite.patterns.behavioral.chainofresponsibility;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Pattern: Chain of Responsibility
// Category: Behavioral
// Problem it solves here: Handler 2 in the pipeline: defends against brute-force attacks by rate limiting endpoints and locking offending IPs/accounts.
// GoF participant role: ConcreteHandler
// Used by: SecurityPipeline
@DesignPattern(name = "Chain of Responsibility", role = "ConcreteHandler", description = "Enforces rate limiting and account brute-force lockouts")
public class RateLimitHandler extends SecurityHandler {
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_WINDOW_SECONDS = 60;

    private static final Map<String, Integer> FAILED_ATTEMPTS = new ConcurrentHashMap<>();
    private static final Map<String, Instant> LOCKOUT_UNTIL = new ConcurrentHashMap<>();

    public static void recordFailure(String key) {
        int attempts = FAILED_ATTEMPTS.merge(key, 1, Integer::sum);
        if (attempts >= MAX_ATTEMPTS) {
            Instant unlockAt = Instant.now().plusSeconds(LOCK_WINDOW_SECONDS);
            LOCKOUT_UNTIL.put(key, unlockAt);
            PatternTracer.getInstance().record(
                "Chain of Responsibility",
                "RateLimitHandler",
                "recordFailure",
                "ACCOUNT/IP LOCKED: '" + key + "' exceeded " + MAX_ATTEMPTS + " failed attempts. Locked for " + LOCK_WINDOW_SECONDS + "s."
            );
        }
    }

    public static void reset(String key) {
        FAILED_ATTEMPTS.remove(key);
        LOCKOUT_UNTIL.remove(key);
    }

    public static long getRemainingLockoutSeconds(String key) {
        Instant unlockAt = LOCKOUT_UNTIL.get(key);
        if (unlockAt == null) return 0;
        long remaining = Instant.now().until(unlockAt, java.time.temporal.ChronoUnit.SECONDS);
        if (remaining <= 0) {
            LOCKOUT_UNTIL.remove(key);
            FAILED_ATTEMPTS.remove(key);
            return 0;
        }
        return remaining;
    }

    @Override
    protected void process(SecurityContext ctx) {
        String key = ctx.getIpAddress();
        long remaining = getRemainingLockoutSeconds(key);

        if (remaining > 0) {
            String msg = "Too Many Requests: IP temporarily locked due to excessive failed attempts. Try again in " + remaining + " seconds.";
            ctx.addTrace("RateLimitHandler", false, msg);
            ctx.terminate(429, msg);
            PatternTracer.getInstance().record(
                "Chain of Responsibility",
                "RateLimitHandler",
                "process",
                "[Step 2] RateLimit: LOCKED (" + remaining + "s remaining) -> TERMINATED 429"
            );
            return;
        }

        ctx.addTrace("RateLimitHandler", true, "IP " + key + " within rate limits (Failures: " + FAILED_ATTEMPTS.getOrDefault(key, 0) + ")");
        PatternTracer.getInstance().record(
            "Chain of Responsibility",
            "RateLimitHandler",
            "process",
            "[Step 2] RateLimit: OK -> PASSED"
        );
    }
}

