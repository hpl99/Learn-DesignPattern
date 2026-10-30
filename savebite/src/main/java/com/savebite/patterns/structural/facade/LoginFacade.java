package com.savebite.patterns.structural.facade;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Role;
import com.savebite.model.User;
import com.savebite.patterns.creational.builder.JwtClaimsBuilder;
import com.savebite.patterns.creational.factorymethod.TokenFactory;
import com.savebite.patterns.structural.adapter.AuthProvider;
import com.savebite.patterns.structural.adapter.AuthResult;
import com.savebite.repository.AuditRepository;
import com.savebite.repository.UserRepository;
import com.savebite.teaching.PatternTracer;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Pattern: Facade
// Category: Structural
// Problem it solves here: User authentication requires orchestrating user repository lookup, BCrypt verification, OAuth adapter dispatch, JWT token minting, claims creation, and audit logging. LoginFacade provides a simple single method to clients.
// GoF participant role: Facade
// Used by: AuthController
@DesignPattern(name = "Facade", role = "Facade", description = "Single unified facade orchestrating authentication subsystems")
public class LoginFacade {
    private final UserRepository userRepository;
    private final AuditRepository auditRepository;
    private final Map<String, AuthProvider> authProviders;

    public record AuthResponse(boolean success, String accessToken, String refreshToken, User user, String errorMessage) {}

    public LoginFacade(UserRepository userRepository, AuditRepository auditRepository, Map<String, AuthProvider> authProviders) {
        this.userRepository = userRepository;
        this.auditRepository = auditRepository;
        this.authProviders = authProviders;
    }

    public AuthResponse loginWithPassword(String email, String rawPassword) {
        PatternTracer.getInstance().record("Facade", "LoginFacade", "loginWithPassword", "Orchestrating login for " + email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            auditRepository.log(email, "LOGIN_FAILED", "User not found");
            return new AuthResponse(false, null, null, null, "Invalid email or password");
        }

        User user = userOpt.get();
        if (!BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
            auditRepository.log(email, "LOGIN_FAILED", "Invalid password");
            return new AuthResponse(false, null, null, null, "Invalid email or password");
        }

        Map<String, Object> claims = new JwtClaimsBuilder()
            .withSubject(user.getId())
            .withRole(user.getRole().name())
            .withFullName(user.getFullName())
            .build();

        String accessToken = TokenFactory.createToken(TokenFactory.TokenType.ACCESS, user.getId(), user.getRole().name(), claims);
        String refreshToken = TokenFactory.createToken(TokenFactory.TokenType.REFRESH, user.getId(), user.getRole().name(), null);

        auditRepository.log(user.getEmail(), "LOGIN_SUCCESS", "Standard password login");
        PatternTracer.getInstance().record("Facade", "LoginFacade", "loginWithPassword", "Login completed successfully for " + user.getEmail());

        return new AuthResponse(true, accessToken, refreshToken, user, null);
    }

    public AuthResponse loginWithOAuth(String providerName, String credential) {
        PatternTracer.getInstance().record("Facade", "LoginFacade", "loginWithOAuth", "Orchestrating OAuth login with " + providerName);

        AuthProvider provider = authProviders.get(providerName.toLowerCase());
        if (provider == null) {
            return new AuthResponse(false, null, null, null, "Unsupported OAuth provider: " + providerName);
        }

        AuthResult authResult = provider.authenticate(credential);
        User user = userRepository.findByEmail(authResult.getEmail()).orElseGet(() -> {
            User newUser = new User(
                "usr-" + UUID.randomUUID().toString().substring(0, 8),
                authResult.getEmail(),
                BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt(10)),
                authResult.getFullName(),
                Role.CUSTOMER,
                "",
                "OAuth Customer",
                Instant.now()
            );
            return userRepository.save(newUser);
        });

        Map<String, Object> claims = new JwtClaimsBuilder()
            .withSubject(user.getId())
            .withRole(user.getRole().name())
            .withFullName(user.getFullName())
            .withCustomClaim("provider", providerName)
            .build();

        String accessToken = TokenFactory.createToken(TokenFactory.TokenType.ACCESS, user.getId(), user.getRole().name(), claims);
        String refreshToken = TokenFactory.createToken(TokenFactory.TokenType.REFRESH, user.getId(), user.getRole().name(), null);

        auditRepository.log(user.getEmail(), "OAUTH_LOGIN", "Provider: " + providerName);
        return new AuthResponse(true, accessToken, refreshToken, user, null);
    }
}

