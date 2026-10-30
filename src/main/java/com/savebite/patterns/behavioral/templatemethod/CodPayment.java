package com.savebite.patterns.behavioral.templatemethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.UUID;

// Pattern: Template Method
// Category: Behavioral
// Problem it solves here: Cash on pickup (COD) skips electronic fund capture, issuing an OTC pay-token to be verified at the bakery counter.
// GoF participant role: ConcreteClass
// Used by: PaymentProcessor
@DesignPattern(name = "Template Method", role = "ConcreteClass", description = "Cash on pickup OTC verification processor")
public class CodPayment extends PaymentProcessor {

    @Override
    public String getMethodName() {
        return "COD";
    }

    @Override
    protected boolean validateDetails(String details) {
        PatternTracer.getInstance().record("Template Method", "CodPayment", "validateDetails", "Verified cash pickup commitment");
        return true;
    }

    @Override
    protected String authorizeAmount(String orderId, double amount) {
        String token = "COD-TOKEN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        PatternTracer.getInstance().record("Template Method", "CodPayment", "authorizeAmount", "Generated counter collection pay-token: " + token);
        return token;
    }

    @Override
    protected boolean captureFunds(String orderId, double amount, String authCode) {
        // Deferred until pickup
        PatternTracer.getInstance().record("Template Method", "CodPayment", "captureFunds", "Deferred fund capture to physical collection time");
        return true;
    }
}

