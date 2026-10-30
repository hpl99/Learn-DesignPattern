package com.savebite.patterns.behavioral.chainofresponsibility;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.creational.singleton.JwtKeyHolder;
import com.savebite.teaching.PatternTracer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

// Pattern: Chain of Responsibility
// Category: Behavioral
// Problem it solves here: Handler 3 in the pipeline: extracts Authorization Bearer token, verifies HMAC signature with JwtKeyHolder, and validates expiration.
// GoF participant role: ConcreteHandler
// Used by: SecurityPipeline
@DesignPattern(name = "Chain of Responsibility", role = "ConcreteHandler", description = "Extracts and cryptographically validates JWT access tokens")
public class JwtAuthenticationHandler extends SecurityHandler {

    @Override
    protected void process(SecurityContext ctx) {
        String authHeader = ctx.getHeader("authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.setAuthenticated(false);
            ctx.addTrace("JwtAuthenticationHandler", true, "No Bearer token present (Guest anonymous access)");
            PatternTracer.getInstance().record(
                "Chain of Responsibility",
                "JwtAuthenticationHandler",
                "process",
                "[Step 3] JWT: Anonymous Guest -> PASSED"
            );
            return;
        }

        String rawToken = authHeader.substring(7).trim();
        try {
            Claims claims = Jwts.parser()
                .verifyWith(JwtKeyHolder.getInstance().getSecretKey())
                .build()
                .parseSignedClaims(rawToken)
                .getPayload();

            ctx.setToken(rawToken);
            ctx.setUserId(claims.getSubject());
            ctx.setUserRole(claims.get("role", String.class));
            ctx.setUserEmail(claims.get("sub", String.class));
            ctx.setJti(claims.getId());
            ctx.setAuthenticated(true);

            ctx.addTrace("JwtAuthenticationHandler", true, "Valid JWT verified for " + claims.getSubject() + " (role: " + ctx.getUserRole() + ", jti: " + ctx.getJti() + ")");
            PatternTracer.getInstance().record(
                "Chain of Responsibility",
                "JwtAuthenticationHandler",
                "process",
                "[Step 3] JWT: Verified user '" + claims.getSubject() + "' (" + ctx.getUserRole() + ") -> PASSED"
            );
        } catch (JwtException e) {
            ctx.setAuthenticated(false);
            ctx.addTrace("JwtAuthenticationHandler", false, "Invalid or expired JWT: " + e.getMessage());
            ctx.terminate(401, "Invalid or expired access token: " + e.getMessage());
            PatternTracer.getInstance().record(
                "Chain of Responsibility",
                "JwtAuthenticationHandler",
                "process",
                "[Step 3] JWT: REJECTED (" + e.getMessage() + ") -> TERMINATED 401"
            );
        }
    }
}

