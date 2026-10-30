package com.savebite.patterns.behavioral.chainofresponsibility;

import com.savebite.annotation.DesignPattern;

// Pattern: Chain of Responsibility
// Category: Behavioral
// Problem it solves here: The HTTP request pipeline must execute a sequence of security checks (RequestId -> RateLimit -> JwtAuthentication -> Blacklist -> RoleAuthorization). Handlers are decoupled and can short-circuit the request if any check fails.
// GoF participant role: Handler
// Used by: RequestIdHandler, RateLimitHandler, JwtAuthenticationHandler, BlacklistHandler, RoleAuthorizationHandler
@DesignPattern(name = "Chain of Responsibility", role = "Handler", description = "Base handler interface in the security pipeline")
public abstract class SecurityHandler {
    private SecurityHandler next;

    public SecurityHandler setNext(SecurityHandler next) {
        this.next = next;
        return next;
    }

    public void handle(SecurityContext ctx) {
        if (ctx.isTerminated()) {
            return;
        }

        process(ctx);

        if (!ctx.isTerminated() && next != null) {
            next.handle(ctx);
        }
    }

    protected abstract void process(SecurityContext ctx);
}

