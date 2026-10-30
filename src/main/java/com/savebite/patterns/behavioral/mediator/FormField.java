package com.savebite.patterns.behavioral.mediator;

import com.savebite.annotation.DesignPattern;

// Pattern: Mediator
// Category: Behavioral
// Problem it solves here: In the checkout form, fields have cascading interdependencies (Country -> State -> City -> Pickup Hub -> Available Slots -> Payment method). Without a mediator, every field would need direct coupling to every other field, producing an unmaintainable $O(N^2)$ web of listeners.
// GoF participant role: Colleague
// Used by: CheckoutMediator, Concrete form fields
@DesignPattern(name = "Mediator", role = "Colleague", description = "Abstract colleague form field in checkout")
public abstract class FormField {
    protected CheckoutMediator mediator;
    protected String value;
    protected boolean enabled = true;

    public void setMediator(CheckoutMediator mediator) {
        this.mediator = mediator;
    }

    public void setValue(String value) {
        this.value = value;
        if (mediator != null) {
            mediator.notify(this, "valueChanged");
        }
    }

    public String getValue() {
        return value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract String getFieldName();
}

