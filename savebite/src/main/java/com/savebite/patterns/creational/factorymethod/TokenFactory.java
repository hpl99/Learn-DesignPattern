package com.savebite.patterns.creational.factorymethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.patterns.creational.singleton.ConfigManager;
import com.savebite.patterns.creational.singleton.JwtKeyHolder;
import com.savebite.teaching.PatternTracer;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

// Pattern: Factory Method
// Category: Creational
// Problem it solves here: Encapsulates creation of short-lived Access Tokens vs long-lived Refresh Tokens with correct expiry, claims, and JTI.
// GoF participant role: Creator
// Used by: JwtService, AuthController
@DesignPattern(name = "Factory Method", role = "Creator", description = "Factory for Access and Refresh JWT tokens")
public class TokenFactory {

    public enum TokenType {
        ACCESS,
        REFRESH
    }

    public static String createToken(TokenType type, String subject, String role, Map<String, Object> extraClaims) {
        long nowMillis = System.currentTimeMillis();
        Date issuedAt = new Date(nowMillis);
        String jti = UUID.randomUUID().toString();

        long expiryMillis = switch (type) {
            case ACCESS -> nowMillis + (ConfigManager.getInstance().getInt("jwt.access.expiry.minutes", 15) * 60 * 1000L);
            case REFRESH -> nowMillis + (ConfigManager.getInstance().getInt("jwt.refresh.expiry.days", 7) * 24 * 60 * 60 * 1000L);
        };

        var builder = Jwts.builder()
            .subject(subject)
            .claim("role", role)
            .claim("type", type.name())
            .id(jti)
            .issuedAt(issuedAt)
            .expiration(new Date(expiryMillis));

        if (extraClaims != null) {
            extraClaims.forEach(builder::claim);
        }

        String token = builder.signWith(JwtKeyHolder.getInstance().getSecretKey()).compact();

        PatternTracer.getInstance().record(
            "Factory Method",
            "TokenFactory",
            "createToken",
            "Minted " + type + " token (jti=" + jti + ", sub=" + subject + ", role=" + role + ")"
        );

        return token;
    }
}

