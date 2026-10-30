package com.savebite.patterns.structural.adapter;

import com.savebite.annotation.DesignPattern;

// Pattern: Adapter
// Category: Structural
// Problem it solves here: Simulates GitHub's REST/GraphQL user endpoint which has distinct keys and types.
// GoF participant role: Adaptee
// Used by: GithubAuthAdapter
@DesignPattern(name = "Adapter", role = "Adaptee", description = "Simulated third-party GitHub OAuth API client")
public class GithubOAuthClient {
    public GithubUser exchangeCodeForUser(String authorizationCode) {
        String code = authorizationCode != null && !authorizationCode.isBlank() ? authorizationCode : "octocat";
        return new GithubUser(
            1234567L,
            code,
            code.toLowerCase() + "@github.user",
            "GitHub Developer (" + code + ")"
        );
    }
}

