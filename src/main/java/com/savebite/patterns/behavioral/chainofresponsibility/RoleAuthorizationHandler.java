package com.savebite.patterns.behavioral.chainofresponsibility;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Chain of Responsibility
// Category: Behavioral
// Problem it solves here: Handler 5 in the pipeline: verifies that the authenticated user possesses the required role permissions (RBAC) to access protected URI endpoints.
// GoF participant role: ConcreteHandler
// Used by: SecurityPipeline
@DesignPattern(name = "Chain of Responsibility", role = "ConcreteHandler", description = "Enforces endpoint role authorization (RBAC)")
public class RoleAuthorizationHandler extends SecurityHandler {

    @Override
    protected void process(SecurityContext ctx) {
        String path = ctx.getPath().toLowerCase();

        // 1. Admin endpoints require ADMIN role
        if (path.startsWith("/api/admin")) {
            if (!ctx.isAuthenticated()) {
                ctx.terminate(401, "Authentication required for admin resources");
                ctx.addTrace("RoleAuthorizationHandler", false, "Unauthenticated access to admin endpoint");
                return;
            }
            if (!"ADMIN".equalsIgnoreCase(ctx.getUserRole())) {
                ctx.terminate(403, "Forbidden: ADMIN role required to access " + ctx.getPath());
                ctx.addTrace("RoleAuthorizationHandler", false, "Role '" + ctx.getUserRole() + "' rejected (requires ADMIN)");
                PatternTracer.getInstance().record(
                    "Chain of Responsibility",
                    "RoleAuthorizationHandler",
                    "process",
                    "[Step 5] RoleAuth: 403 Forbidden for non-admin role '" + ctx.getUserRole() + "' -> TERMINATED"
                );
                return;
            }
        }

        // 2. Seller endpoints require SELLER or ADMIN role
        if (path.startsWith("/api/seller")) {
            if (!ctx.isAuthenticated()) {
                ctx.terminate(401, "Authentication required for seller resources");
                ctx.addTrace("RoleAuthorizationHandler", false, "Unauthenticated access to seller endpoint");
                return;
            }
            if (!"SELLER".equalsIgnoreCase(ctx.getUserRole()) && !"ADMIN".equalsIgnoreCase(ctx.getUserRole())) {
                ctx.terminate(403, "Forbidden: SELLER role required to access " + ctx.getPath());
                ctx.addTrace("RoleAuthorizationHandler", false, "Role '" + ctx.getUserRole() + "' rejected (requires SELLER)");
                PatternTracer.getInstance().record(
                    "Chain of Responsibility",
                    "RoleAuthorizationHandler",
                    "process",
                    "[Step 5] RoleAuth: 403 Forbidden for customer -> TERMINATED"
                );
                return;
            }
        }

        // 3. Customer orders & checkout require at least authenticated customer
        if (path.startsWith("/api/orders") || path.startsWith("/api/checkout") || path.startsWith("/api/profile")) {
            if (!ctx.isAuthenticated()) {
                ctx.terminate(401, "Authentication required to access personal profile or orders");
                ctx.addTrace("RoleAuthorizationHandler", false, "Unauthenticated access to customer endpoint");
                return;
            }
        }

        ctx.addTrace("RoleAuthorizationHandler", true, "Authorization granted for path " + ctx.getPath() + " (role: " + (ctx.getUserRole() != null ? ctx.getUserRole() : "ANONYMOUS") + ")");
        PatternTracer.getInstance().record(
            "Chain of Responsibility",
            "RoleAuthorizationHandler",
            "process",
            "[Step 5] RoleAuth: OK -> PASSED"
        );
    }
}

