package com.savebite.security;

import com.savebite.patterns.behavioral.chainofresponsibility.SecurityContext;
import com.savebite.patterns.behavioral.chainofresponsibility.SecurityPipeline;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SecurityFilter implements Handler {
    private final SecurityPipeline pipeline;

    public SecurityFilter(SecurityPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        // Allow static assets directly
        String path = ctx.path();
        if (isStaticAsset(path)) {
            return;
        }

        Map<String, String> headers = new HashMap<>();
        ctx.headerMap().forEach(headers::put);

        String ip = ctx.ip();
        SecurityContext secCtx = new SecurityContext(path, ctx.method().name(), ip, headers);

        pipeline.execute(secCtx);

        // Security response headers
        ctx.header("X-Content-Type-Options", "nosniff");
        ctx.header("X-Frame-Options", "DENY");
        ctx.header("X-XSS-Protection", "1; mode=block");
        if (secCtx.getHeader("x-request-id") != null) {
            ctx.header("X-Request-ID", secCtx.getHeader("x-request-id"));
        }

        if (secCtx.isTerminated()) {
            Map<String, Object> errorShape = Map.of(
                "timestamp", Instant.now().toString(),
                "status", secCtx.getHttpStatus(),
                "error", secCtx.getHttpStatus() == 401 ? "Unauthorized" : (secCtx.getHttpStatus() == 403 ? "Forbidden" : (secCtx.getHttpStatus() == 429 ? "Too Many Requests" : "Bad Request")),
                "message", secCtx.getErrorMessage() != null ? secCtx.getErrorMessage() : "Request blocked by security pipeline",
                "path", path
            );
            ctx.status(secCtx.getHttpStatus()).json(errorShape);
            ctx.skipRemainingHandlers();
            return;
        }

        // Store user in request context for downstream handlers
        if (secCtx.isAuthenticated()) {
            ctx.attribute("userId", secCtx.getUserId());
            ctx.attribute("userRole", secCtx.getUserRole());
            ctx.attribute("userEmail", secCtx.getUserEmail());
        }
    }

    private boolean isStaticAsset(String path) {
        return path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js") ||
               path.endsWith(".svg") || path.endsWith(".png") || path.endsWith(".ico") ||
               path.equals("/") || path.startsWith("/css/") || path.startsWith("/js/") ||
               path.startsWith("/img/");
    }
}

