package com.savebite.patterns.creational.singleton;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

// Pattern: Singleton
// Category: Creational
// Problem it solves here: The cryptographic HMAC key for signing and verifying JWTs must be generated once from secret configuration and securely held in memory.
// GoF participant role: Singleton
// Used by: JwtService, Auth filters
@DesignPattern(name = "Singleton", role = "Singleton", description = "Singleton holding cryptographic key for JWT operations")
public class JwtKeyHolder {
    private final SecretKey secretKey;

    private JwtKeyHolder() {
        String secret = ConfigManager.getInstance().get("jwt.secret");
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        PatternTracer.getInstance().record(
            "Singleton",
            "JwtKeyHolder",
            "<init>",
            "HMAC-SHA256 SecretKey successfully instantiated and held in memory."
        );
    }

    private static class Holder {
        private static final JwtKeyHolder INSTANCE = new JwtKeyHolder();
    }

    public static JwtKeyHolder getInstance() {
        return Holder.INSTANCE;
    }

    public SecretKey getSecretKey() {
        PatternTracer.getInstance().record(
            "Singleton",
            "JwtKeyHolder",
            "getSecretKey",
            "Providing HMAC key instance for JWT operation"
        );
        return secretKey;
    }

    public String getInstanceId() {
        return "JwtKeyHolder@" + Integer.toHexString(System.identityHashCode(this));
    }
}

