package com.savebite.patterns.structural.decorator;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Decorator
// Category: Structural
// Problem it solves here: Applies promotional coupon discounts (e.g. 15% off) dynamically to whatever pricing composition currently exists.
// GoF participant role: ConcreteDecorator
// Used by: CheckoutFacade
@DesignPattern(name = "Decorator", role = "ConcreteDecorator", description = "Applies percentage promo discount to price")
public class CouponDecorator extends PriceDecorator {
    private final String couponCode;
    private final double discountPercentage;

    public CouponDecorator(Price wrappee, String couponCode, double discountPercentage) {
        super(wrappee);
        this.couponCode = couponCode;
        this.discountPercentage = discountPercentage;
    }

    @Override
    public double getAmount() {
        double current = wrappee.getAmount();
        double discounted = Math.max(0.0, current * (1.0 - (discountPercentage / 100.0)));
        double total = Math.round(discounted * 100.0) / 100.0;
        PatternTracer.getInstance().record(
            "Decorator",
            "CouponDecorator",
            "getAmount",
            "Applied Coupon [" + couponCode + " -" + (int)discountPercentage + "%] on ₹" + current + ". New total: ₹" + total
        );
        return total;
    }

    @Override
    public String getDescription() {
        return wrappee.getDescription() + " - Coupon " + couponCode + " (" + (int)discountPercentage + "% off)";
    }
}

