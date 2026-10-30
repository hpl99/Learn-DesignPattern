package com.savebite.patterns.structural.adapter;

import com.savebite.annotation.DesignPattern;

// Pattern: Adapter
// Category: Structural
// Problem it solves here: Legacy or third-party Google Identity Services SDK with proprietary API.
// GoF participant role: Adaptee
// Used by: GoogleAuthAdapter
@DesignPattern(name = "Adapter", role = "Adaptee", description = "Simulated third-party Google Identity SDK")
public class GoogleOAuthClient {
    public GoogleProfile verifyIdTokenAndFetchProfile(String idToken) {
        // Simulated Google token verification
        String user = idToken != null && !idToken.isBlank() ? idToken : "customer";
        return new GoogleProfile(
            "google-usr-987654",
            user.toLowerCase().replaceAll("[^a-z0-9]", "") + "@gmail.com",
            "Google User " + user
        );
    }
}

