package com.savebite.patterns.structural.adapter;

import com.savebite.annotation.DesignPattern;

// Pattern: Adapter
// Category: Structural
// Problem it solves here: SaveBite requires a single target authentication interface, but third-party OAuth SDKs (Google, GitHub) expose wildly different method names, parameter structures, and response payloads.
// GoF participant role: Target
// Used by: LoginFacade, AuthController
@DesignPattern(name = "Adapter", role = "Target", description = "Unified target interface for external identity providers")
public interface AuthProvider {
    String getProviderName();
    AuthResult authenticate(String credentialOrCode);
}

