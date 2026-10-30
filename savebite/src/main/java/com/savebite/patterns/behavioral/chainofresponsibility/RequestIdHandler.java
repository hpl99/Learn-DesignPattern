package com.savebite.patterns.behavioral.chainofresponsibility;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.UUID;

// Pattern: Chain of Responsibility
// Category: Behavioral
// Problem it solves here: Handler 1 in the pipeline: ensures every incoming request has a unique distributed trace request ID.
// GoF participant role: ConcreteHandler
// Used by: SecurityPipeline
@DesignPattern(name = "Chain of Responsibility", role = "ConcreteHandler", description = "Extracts or generates unique X-Request-ID")
public class RequestIdHandler extends SecurityHandler {
    @Override
    protected void process(SecurityContext ctx) {
        String reqId = ctx.getHeader("x-request-id");
        if (reqId == null || reqId.isBlank()) {
            reqId = "req-" + UUID.randomUUID().toString().substring(0, 8);
            ctx.setHeader("x-request-id", reqId);
        }

        ctx.addTrace("RequestIdHandler", true, "Request ID assigned: " + reqId);
        PatternTracer.getInstance().record(
            "Chain of Responsibility",
            "RequestIdHandler",
            "process",
            "[Step 1] Request ID: " + reqId + " -> PASSED"
        );
    }
}

