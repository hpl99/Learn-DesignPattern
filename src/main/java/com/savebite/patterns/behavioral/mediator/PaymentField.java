package com.savebite.patterns.behavioral.mediator;

import com.savebite.annotation.DesignPattern;
import java.util.List;

@DesignPattern(name = "Mediator", role = "ConcreteColleague", description = "Payment method selection colleague")
public class PaymentField extends FormField {
    private final List<String> methods = List.of("UPI", "CARD", "COD");

    public PaymentField() {
        this.value = "UPI";
    }

    @Override
    public String getFieldName() { return "payment"; }

    public List<String> getMethods() { return methods; }
}

