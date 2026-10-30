package com.savebite.patterns.structural.adapter;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Adapter
// Category: Structural
// Problem it solves here: Adapts Google's proprietary verifyIdTokenAndFetchProfile() signature into SaveBite's uniform AuthProvider interface.
// GoF participant role: Adapter
// Used by: LoginFacade
@DesignPattern(name = "Adapter", role = "Adapter", description = "Adapter converting Google Identity SDK to AuthProvider")
public class GoogleAuthAdapter implements AuthProvider {
    private final GoogleOAuthClient googleClient;

    public GoogleAuthAdapter() {
        this.googleClient = new GoogleOAuthClient();
    }

    public GoogleAuthAdapter(GoogleOAuthClient client) {
        this.googleClient = client;
    }

    @Override
    public String getProviderName() {
        return "google";
    }

    @Override
    public AuthResult authenticate(String credentialOrCode) {
        GoogleProfile profile = googleClient.verifyIdTokenAndFetchProfile(credentialOrCode);
        AuthResult result = new AuthResult(profile.email, profile.name, "google", profile.sub);

        PatternTracer.getInstance().record(
            "Adapter",
            "GoogleAuthAdapter",
            "authenticate",
            "Adapted Google profile [" + profile.email + "] into unified AuthResult"
        );

        return result;
    }
}

