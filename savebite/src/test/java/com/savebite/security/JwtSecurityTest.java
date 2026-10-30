package com.savebite.security;

import com.savebite.patterns.behavioral.chainofresponsibility.BlacklistHandler;
import com.savebite.patterns.creational.singleton.JwtKeyHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtSecurityTest {
    private final JwtService jwtService = new JwtService();

    @Test
    @DisplayName("JWT: generate valid access token and parse claims")
    void testValidToken() {
        String token = jwtService.generateAccessToken("usr-123", "CUSTOMER", Map.of("email", "customer@savebite.org"));
        assertThat(token).isNotBlank();

        Claims claims = jwtService.parseAndValidate(token);
        assertThat(claims.getSubject()).isEqualTo("usr-123");
        assertThat(claims.get("role", String.class)).isEqualTo("CUSTOMER");
        assertThat(claims.get("email", String.class)).isEqualTo("customer@savebite.org");
        assertThat(claims.getId()).isNotNull();
    }

    @Test
    @DisplayName("JWT: expired token must be rejected")
    void testExpiredToken() {
        SecretKey key = JwtKeyHolder.getInstance().getSecretKey();
        String expiredToken = Jwts.builder()
            .subject("usr-expired")
            .claim("role", "CUSTOMER")
            .id(UUID.randomUUID().toString())
            .issuedAt(new Date(System.currentTimeMillis() - 100000))
            .expiration(new Date(System.currentTimeMillis() - 50000)) // expired in past
            .signWith(key)
            .compact();

        assertThatThrownBy(() -> jwtService.parseAndValidate(expiredToken))
            .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("JWT: tampered token signature must be rejected")
    void testTamperedToken() {
        String token = jwtService.generateAccessToken("usr-123", "CUSTOMER", null);
        // Tamper with payload characters
        String tamperedToken = token.substring(0, token.length() - 8) + "badchars";

        assertThatThrownBy(() -> jwtService.parseAndValidate(tamperedToken))
            .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("JWT: token signed with foreign key must be rejected")
    void testWrongKeySignature() {
        SecretKey foreignKey = Keys.hmacShaKeyFor("TotallyDifferentSecretKeyMustBe32BytesLong123!".getBytes(StandardCharsets.UTF_8));
        String rogueToken = Jwts.builder()
            .subject("attacker")
            .claim("role", "ADMIN")
            .id(UUID.randomUUID().toString())
            .expiration(new Date(System.currentTimeMillis() + 60000))
            .signWith(foreignKey)
            .compact();

        assertThatThrownBy(() -> jwtService.parseAndValidate(rogueToken))
            .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("JWT: logout revokes token and blacklists JTI")
    void testBlacklistRevocation() {
        String token = jwtService.generateAccessToken("usr-logout", "CUSTOMER", null);
        Claims claims = jwtService.parseAndValidate(token);
        String jti = claims.getId();

        assertThat(BlacklistHandler.isRevoked(jti)).isFalse();

        // Perform revocation
        jwtService.revokeToken(token);
        assertThat(BlacklistHandler.isRevoked(jti)).isTrue();
    }

    @Test
    @DisplayName("JWT: refresh token rotation produces new valid token and invalidates old token")
    void testRefreshTokenRotation() {
        String refreshToken = jwtService.generateRefreshToken("usr-rotation", "CUSTOMER");
        Claims initialClaims = jwtService.parseAndValidate(refreshToken);
        String initialJti = initialClaims.getId();

        // Revoke old and generate new
        jwtService.revokeToken(refreshToken);
        String newRefreshToken = jwtService.generateRefreshToken("usr-rotation", "CUSTOMER");
        Claims newClaims = jwtService.parseAndValidate(newRefreshToken);

        assertThat(BlacklistHandler.isRevoked(initialJti)).isTrue();
        assertThat(BlacklistHandler.isRevoked(newClaims.getId())).isFalse();
        assertThat(newClaims.getSubject()).isEqualTo("usr-rotation");
    }
}

