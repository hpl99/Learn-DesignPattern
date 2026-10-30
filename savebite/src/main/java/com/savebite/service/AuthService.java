package com.savebite.service;

import com.savebite.model.Role;
import com.savebite.model.User;
import com.savebite.patterns.behavioral.chainofresponsibility.RateLimitHandler;
import com.savebite.patterns.structural.facade.LoginFacade;
import com.savebite.repository.UserRepository;
import com.savebite.security.JwtService;
import io.jsonwebtoken.Claims;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepository;
    private final LoginFacade loginFacade;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, LoginFacade loginFacade, JwtService jwtService) {
        this.userRepository = userRepository;
        this.loginFacade = loginFacade;
        this.jwtService = jwtService;
    }

    public LoginFacade.AuthResponse login(String email, String password, String ip) {
        LoginFacade.AuthResponse res = loginFacade.loginWithPassword(email, password);
        if (!res.success()) {
            RateLimitHandler.recordFailure(ip);
        } else {
            RateLimitHandler.reset(ip);
        }
        return res;
    }

    public LoginFacade.AuthResponse loginOAuth(String provider, String token) {
        return loginFacade.loginWithOAuth(provider, token);
    }

    public User register(String email, String rawPassword, String fullName, Role role, String phone, String address) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists.");
        }
        String passwordHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
        User user = new User(
            "usr-" + UUID.randomUUID().toString().substring(0, 8),
            email,
            passwordHash,
            fullName,
            role != null ? role : Role.CUSTOMER,
            phone,
            address,
            Instant.now()
        );
        return userRepository.save(user);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public String rotateRefreshToken(String oldRefreshToken) {
        Claims claims = jwtService.parseAndValidate(oldRefreshToken);
        jwtService.revokeToken(oldRefreshToken);
        return jwtService.generateRefreshToken(claims.getSubject(), claims.get("role", String.class));
    }

    public String generateAccessTokenFromRefresh(String refreshToken) {
        Claims claims = jwtService.parseAndValidate(refreshToken);
        return jwtService.generateAccessToken(claims.getSubject(), claims.get("role", String.class), null);
    }

    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null) {
            jwtService.revokeToken(accessToken);
        }
        if (refreshToken != null) {
            jwtService.revokeToken(refreshToken);
        }
    }
}

