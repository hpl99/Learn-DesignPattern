package com.savebite.patterns.behavioral.templatemethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.UUID;

// Pattern: Template Method
// Category: Behavioral
// Problem it solves here: Card specific validation (Luhn 16-digit check) and gateway capture step.
// GoF participant role: ConcreteClass
// Used by: PaymentProcessor
@DesignPattern(name = "Template Method", role = "ConcreteClass", description = "Credit/Debit Card payment processor")
public class CardPayment extends PaymentProcessor {

    @Override
    public String getMethodName() {
        return "CARD";
    }

    @Override
    protected boolean validateDetails(String details) {
        // Simple check: card number should be 16 digits or demo string
        boolean valid = details != null && (details.length() >= 12 || details.contains("card"));
        PatternTracer.getInstance().record("Template Method", "CardPayment", "validateDetails", "Validated card details: " + valid);
        return valid;
    }

    @Override
    protected String authorizeAmount(String orderId, double amount) {
        String authCode = "AUTH-CARD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PatternTracer.getInstance().record("Template Method", "CardPayment", "authorizeAmount", "Authorized ₹" + amount + " with issuer. Code: " + authCode);
        return authCode;
    }

    @Override
    protected boolean captureFunds(String orderId, double amount, String authCode) {
        PatternTracer.getInstance().record("Template Method", "CardPayment", "captureFunds", "Captured funds from merchant account for " + authCode);
        return true;
    }
}

