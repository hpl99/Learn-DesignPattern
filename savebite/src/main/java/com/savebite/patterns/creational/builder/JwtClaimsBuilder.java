package com.savebite.patterns.creational.builder;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.HashMap;
import java.util.Map;

// Pattern: Builder
// Category: Creational
// Problem it solves here: Assembles custom JWT payload claims (email, roles, permissions, tenantId, teaching mode) safely without mutating claims directly.
// GoF participant role: Builder
// Used by: JwtService, AuthController
@DesignPattern(name = "Builder", role = "Builder", description = "Builder for constructing JWT claims maps")
public class JwtClaimsBuilder {
    private final Map<String, Object> claims = new HashMap<>();

    public JwtClaimsBuilder withSubject(String subject) {
        claims.put("sub", subject);
        return this;
    }

    public JwtClaimsBuilder withRole(String role) {
        claims.put("role", role);
        return this;
    }

    public JwtClaimsBuilder withFullName(String fullName) {
        claims.put("name", fullName);
        return this;
    }

    public JwtClaimsBuilder withCustomClaim(String key, Object value) {
        if (key != null && value != null) {
            claims.put(key, value);
        }
        return this;
    }

    public Map<String, Object> build() {
        PatternTracer.getInstance().record(
            "Builder",
            "JwtClaimsBuilder",
            "build",
            "Built JWT claims map with " + claims.size() + " entries"
        );
        return new HashMap<>(claims);
    }
}

