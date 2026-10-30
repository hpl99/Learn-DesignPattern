package com.savebite.patterns.behavioral.chainofresponsibility;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Pattern: Chain of Responsibility
// Category: Behavioral
// Problem it solves here: Handler 4 in the pipeline: checks whether a logged-out JWT's unique token ID (jti) has been blacklisted before expiration.
// GoF participant role: ConcreteHandler
// Used by: SecurityPipeline, AuthController
@DesignPattern(name = "Chain of Responsibility", role = "ConcreteHandler", description = "Checks JWT ID (jti) against revoked token blacklist")
public class BlacklistHandler extends SecurityHandler {
    private static final Map<String, Instant> BLACKLIST = new ConcurrentHashMap<>();

    public static void revokeToken(String jti, Instant expiresAt) {
        if (jti != null && !jti.isBlank()) {
            BLACKLIST.put(jti, expiresAt != null ? expiresAt : Instant.now().plusSeconds(3600));
            PatternTracer.getInstance().record(
                "Chain of Responsibility",
                "BlacklistHandler",
                "revokeToken",
                "Blacklisted token jti=" + jti + " until " + expiresAt
            );
        }
    }

    public static boolean isRevoked(String jti) {
        if (jti == null) return false;
        Instant exp = BLACKLIST.get(jti);
        if (exp == null) return false;
        if (Instant.now().isAfter(exp)) {
            BLACKLIST.remove(jti);
            return false;
        }
        return true;
    }

    public static int getBlacklistSize() {
        return BLACKLIST.size();
    }

    @Override
    protected void process(SecurityContext ctx) {
        if (!ctx.isAuthenticated() || ctx.getJti() == null) {
            ctx.addTrace("BlacklistHandler", true, "Skipped blacklist check (unauthenticated/no jti)");
            return;
        }

        if (isRevoked(ctx.getJti())) {
            String msg = "Unauthorized: Access token has been revoked (logged out). Please log in again.";
            ctx.addTrace("BlacklistHandler", false, msg);
            ctx.terminate(401, msg);
            PatternTracer.getInstance().record(
                "Chain of Responsibility",
                "BlacklistHandler",
                "process",
                "[Step 4] Blacklist: Token jti=" + ctx.getJti() + " is REVOKED -> TERMINATED 401"
            );
            return;
        }

        ctx.addTrace("BlacklistHandler", true, "Token jti=" + ctx.getJti() + " is clean (not blacklisted)");
        PatternTracer.getInstance().record(
            "Chain of Responsibility",
            "BlacklistHandler",
            "process",
            "[Step 4] Blacklist: Clean -> PASSED"
        );
    }
}

