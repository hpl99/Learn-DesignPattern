package com.savebite.patterns.behavioral.mediator;

import com.savebite.annotation.DesignPattern;
import java.util.ArrayList;
import java.util.List;

@DesignPattern(name = "Mediator", role = "ConcreteColleague", description = "Pickup Hub location selector colleague")
public class PickupPointField extends FormField {
    private List<String> options = new ArrayList<>();

    public PickupPointField() {
        this.value = "Indiranagar Hub";
        this.options = List.of("Indiranagar Hub", "Koramangala Depot", "HSR Layout Store");
    }

    @Override
    public String getFieldName() { return "pickupPoint"; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}

