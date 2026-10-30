package com.savebite.controller;

import com.savebite.model.Role;
import com.savebite.model.User;
import com.savebite.patterns.behavioral.chainofresponsibility.RateLimitHandler;
import com.savebite.patterns.structural.facade.LoginFacade;
import com.savebite.service.AuthService;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.SameSite;

import java.util.Map;
import java.util.Optional;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void login(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            ctx.status(400).json(Map.of("error", "Email and password are required."));
            return;
        }

        String ip = ctx.ip();
        long remainingLock = RateLimitHandler.getRemainingLockoutSeconds(ip);
        if (remainingLock > 0) {
            ctx.status(429).json(Map.of(
                "error", "Account locked",
                "message", "Too many failed attempts. Locked for " + remainingLock + " seconds.",
                "remainingSeconds", remainingLock
            ));
            return;
        }

        LoginFacade.AuthResponse response = authService.login(email, password, ip);
        if (!response.success()) {
            ctx.status(401).json(Map.of("error", response.errorMessage()));
            return;
        }

        // Set HttpOnly refresh token cookie
        Cookie cookie = new Cookie("refreshToken", response.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setSameSite(SameSite.STRICT);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        ctx.cookie(cookie);

        ctx.json(Map.of(
            "accessToken", response.accessToken(),
            "user", response.user()
        ));
    }

    public void loginOAuth(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String provider = body.getOrDefault("provider", "google");
        String token = body.getOrDefault("token", "demo-token");

        LoginFacade.AuthResponse response = authService.loginOAuth(provider, token);
        if (!response.success()) {
            ctx.status(400).json(Map.of("error", response.errorMessage()));
            return;
        }

        Cookie cookie = new Cookie("refreshToken", response.refreshToken());
        cookie.setHttpOnly(true);
        cookie.setSameSite(SameSite.STRICT);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        ctx.cookie(cookie);

        ctx.json(Map.of(
            "accessToken", response.accessToken(),
            "user", response.user()
        ));
    }

    public void register(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String email = body.get("email");
        String password = body.get("password");
        String fullName = body.get("fullName");
        String roleStr = body.getOrDefault("role", "CUSTOMER");
        String phone = body.getOrDefault("phone", "");
        String address = body.getOrDefault("address", "");

        if (email == null || password == null || fullName == null || password.length() < 6) {
            ctx.status(400).json(Map.of("error", "Valid email, full name, and minimum 6-character password required."));
            return;
        }

        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            User user = authService.register(email, password, fullName, role, phone, address);
            ctx.status(201).json(user);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    public void refresh(Context ctx) {
        String refreshToken = ctx.cookie("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            ctx.status(401).json(Map.of("error", "No refresh token cookie present."));
            return;
        }

        try {
            String newRefreshToken = authService.rotateRefreshToken(refreshToken);
            String newAccessToken = authService.generateAccessTokenFromRefresh(newRefreshToken);

            Cookie cookie = new Cookie("refreshToken", newRefreshToken);
            cookie.setHttpOnly(true);
            cookie.setSameSite(SameSite.STRICT);
            cookie.setPath("/api/auth");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            ctx.cookie(cookie);

            ctx.json(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            ctx.status(401).json(Map.of("error", "Invalid or expired refresh token: " + e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        String authHeader = ctx.header("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        String refreshToken = ctx.cookie("refreshToken");

        authService.logout(accessToken, refreshToken);

        // Clear cookie
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        ctx.cookie(cookie);

        ctx.json(Map.of("message", "Logged out successfully"));
    }

    public void me(Context ctx) {
        String userId = ctx.attribute("userId");
        if (userId == null) {
            ctx.status(401).json(Map.of("error", "Not authenticated"));
            return;
        }

        Optional<User> user = authService.findById(userId);
        if (user.isPresent()) {
            ctx.json(user.get());
        } else {
            ctx.status(404).json(Map.of("error", "User not found"));
        }
    }
}

