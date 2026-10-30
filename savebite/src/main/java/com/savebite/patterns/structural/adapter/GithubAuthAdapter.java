package com.savebite.patterns.structural.adapter;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Adapter
// Category: Structural
// Problem it solves here: Adapts GitHub's exchangeCodeForUser() response into SaveBite's uniform AuthProvider interface.
// GoF participant role: Adapter
// Used by: LoginFacade
@DesignPattern(name = "Adapter", role = "Adapter", description = "Adapter converting GitHub OAuth to AuthProvider")
public class GithubAuthAdapter implements AuthProvider {
    private final GithubOAuthClient githubClient;

    public GithubAuthAdapter() {
        this.githubClient = new GithubOAuthClient();
    }

    public GithubAuthAdapter(GithubOAuthClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public String getProviderName() {
        return "github";
    }

    @Override
    public AuthResult authenticate(String credentialOrCode) {
        GithubUser ghUser = githubClient.exchangeCodeForUser(credentialOrCode);
        AuthResult result = new AuthResult(ghUser.userEmail, ghUser.displayName, "github", String.valueOf(ghUser.id));

        PatternTracer.getInstance().record(
            "Adapter",
            "GithubAuthAdapter",
            "authenticate",
            "Adapted GitHub account [" + ghUser.userEmail + "] into unified AuthResult"
        );

        return result;
    }
}

