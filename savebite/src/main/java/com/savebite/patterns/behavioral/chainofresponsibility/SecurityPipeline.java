package com.savebite.patterns.behavioral.chainofresponsibility;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.concurrent.atomic.AtomicReference;

// Pattern: Chain of Responsibility
// Category: Behavioral
// Problem it solves here: Assembles and initiates the security handler pipeline in strict order: RequestId -> RateLimit -> JwtAuthentication -> Blacklist -> RoleAuthorization. Captures the last execution report for live visual proofs.
// GoF participant role: Client / Pipeline
// Used by: Javalin before-filter, TeachingController
@DesignPattern(name = "Chain of Responsibility", role = "Client", description = "Security pipeline orchestrator linking handlers")
public class SecurityPipeline {
    private final SecurityHandler firstHandler;
    private static final AtomicReference<SecurityContext> LAST_EXECUTED_CONTEXT = new AtomicReference<>();

    public SecurityPipeline() {
        RequestIdHandler reqId = new RequestIdHandler();
        RateLimitHandler rateLimit = new RateLimitHandler();
        JwtAuthenticationHandler jwt = new JwtAuthenticationHandler();
        BlacklistHandler blacklist = new BlacklistHandler();
        RoleAuthorizationHandler role = new RoleAuthorizationHandler();

        reqId.setNext(rateLimit)
             .setNext(jwt)
             .setNext(blacklist)
             .setNext(role);

        this.firstHandler = reqId;
    }

    public SecurityContext execute(SecurityContext ctx) {
        PatternTracer.getInstance().record(
            "Chain of Responsibility",
            "SecurityPipeline",
            "execute",
            "Beginning security pipeline execution for " + ctx.getMethod() + " " + ctx.getPath()
        );

        firstHandler.handle(ctx);
        LAST_EXECUTED_CONTEXT.set(ctx);

        return ctx;
    }

    public static SecurityContext getLastExecutedContext() {
        return LAST_EXECUTED_CONTEXT.get();
    }
}

