package com.savebite.patterns.behavioral.templatemethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.UUID;

// Pattern: Template Method
// Category: Behavioral
// Problem it solves here: UPI specific VPA handle validation (e.g. user@okaxis) and instant bank settlement.
// GoF participant role: ConcreteClass
// Used by: PaymentProcessor
@DesignPattern(name = "Template Method", role = "ConcreteClass", description = "UPI instant payment processor")
public class UpiPayment extends PaymentProcessor {

    @Override
    public String getMethodName() {
        return "UPI";
    }

    @Override
    protected boolean validateDetails(String details) {
        boolean valid = details != null && (details.contains("@") || details.equalsIgnoreCase("upi"));
        PatternTracer.getInstance().record("Template Method", "UpiPayment", "validateDetails", "Validated UPI VPA format: " + valid);
        return valid;
    }

    @Override
    protected String authorizeAmount(String orderId, double amount) {
        String authCode = "UPI-NPCI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PatternTracer.getInstance().record("Template Method", "UpiPayment", "authorizeAmount", "NPCI UPI authorized ₹" + amount + ". RRN: " + authCode);
        return authCode;
    }

    @Override
    protected boolean captureFunds(String orderId, double amount, String authCode) {
        PatternTracer.getInstance().record("Template Method", "UpiPayment", "captureFunds", "Direct debit via IMPS/UPI instant settlement");
        return true;
    }
}

