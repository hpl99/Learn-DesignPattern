package com.savebite.security;

import com.savebite.patterns.behavioral.chainofresponsibility.BlacklistHandler;
import com.savebite.patterns.creational.factorymethod.TokenFactory;
import com.savebite.patterns.creational.singleton.JwtKeyHolder;
import com.savebite.teaching.PatternTracer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.time.Instant;
import java.util.Map;

public class JwtService {

    public String generateAccessToken(String userId, String role, Map<String, Object> extraClaims) {
        return TokenFactory.createToken(TokenFactory.TokenType.ACCESS, userId, role, extraClaims);
    }

    public String generateRefreshToken(String userId, String role) {
        return TokenFactory.createToken(TokenFactory.TokenType.REFRESH, userId, role, null);
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
            .verifyWith(JwtKeyHolder.getInstance().getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public void revokeToken(String token) {
        try {
            Claims claims = parseAndValidate(token);
            String jti = claims.getId();
            Instant exp = claims.getExpiration().toInstant();
            BlacklistHandler.revokeToken(jti, exp);
            PatternTracer.getInstance().record("Chain of Responsibility", "JwtService", "revokeToken", "Revoked token jti: " + jti);
        } catch (Exception ignored) {}
    }
}

