package com.savebite.patterns.behavioral.templatemethod;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.UUID;

// Pattern: Template Method
// Category: Behavioral
// Problem it solves here: Payment processing follows a mandatory strict lifecycle invariant: validate -> authorize -> capture -> receipt. Individual methods differ (Credit Card vs UPI vs COD), but the structural execution sequence must never be altered.
// GoF participant role: AbstractClass
// Used by: CardPayment, UpiPayment, CodPayment, CheckoutFacade
@DesignPattern(name = "Template Method", role = "AbstractClass", description = "Defines invariant skeleton for payment processing workflow")
public abstract class PaymentProcessor {

    public record PaymentResult(boolean success, String transactionId, String paymentMethod, String receipt, String message) {}

    /**
     * The Template Method: final to prevent subclasses from altering the invariant sequence.
     */
    public final PaymentResult processPayment(String orderId, double amount, String paymentDetails) {
        PatternTracer.getInstance().record(
            "Template Method",
            getClass().getSimpleName(),
            "processPayment",
            "Executing invariant payment skeleton for Order " + orderId + " (₹" + amount + ")"
        );

        // Step 1: Validate
        if (!validateDetails(paymentDetails)) {
            PatternTracer.getInstance().record("Template Method", getClass().getSimpleName(), "validateDetails", "FAILED validation: " + paymentDetails);
            return new PaymentResult(false, null, getMethodName(), null, "Payment details validation failed");
        }

        // Step 2: Authorize
        String authCode = authorizeAmount(orderId, amount);
        if (authCode == null) {
            PatternTracer.getInstance().record("Template Method", getClass().getSimpleName(), "authorizeAmount", "FAILED authorization for ₹" + amount);
            return new PaymentResult(false, null, getMethodName(), null, "Authorization declined");
        }

        // Step 3: Capture / Lock
        boolean captured = captureFunds(orderId, amount, authCode);
        if (!captured) {
            PatternTracer.getInstance().record("Template Method", getClass().getSimpleName(), "captureFunds", "FAILED fund capture");
            return new PaymentResult(false, null, getMethodName(), null, "Fund capture failed");
        }

        // Step 4: Generate Receipt
        String receipt = generateReceipt(orderId, amount, authCode);

        PatternTracer.getInstance().record(
            "Template Method",
            getClass().getSimpleName(),
            "processPayment",
            "SUCCESS: Payment completed via " + getMethodName() + " (Auth=" + authCode + ")"
        );

        return new PaymentResult(true, authCode, getMethodName(), receipt, "Payment successful");
    }

    public abstract String getMethodName();
    protected abstract boolean validateDetails(String details);
    protected abstract String authorizeAmount(String orderId, double amount);
    protected abstract boolean captureFunds(String orderId, double amount, String authCode);

    // Hook / default implementation that can be overridden
    protected String generateReceipt(String orderId, double amount, String authCode) {
        return String.format("[RECEIPT] Order: %s | Amount: ₹%.2f | Method: %s | AuthCode: %s",
            orderId, amount, getMethodName(), authCode);
    }
}

