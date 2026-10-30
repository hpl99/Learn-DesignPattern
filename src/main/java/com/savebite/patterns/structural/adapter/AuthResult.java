package com.savebite.patterns.structural.adapter;

public class AuthResult {
    private final String email;
    private final String fullName;
    private final String provider;
    private final String providerUserId;

    public AuthResult(String email, String fullName, String provider, String providerUserId) {
        this.email = email;
        this.fullName = fullName;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }

    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getProvider() { return provider; }
    public String getProviderUserId() { return providerUserId; }
}

