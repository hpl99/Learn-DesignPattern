package com.savebite.patterns.behavioral.mediator;

import com.savebite.annotation.DesignPattern;
import java.util.ArrayList;
import java.util.List;

@DesignPattern(name = "Mediator", role = "ConcreteColleague", description = "Time slot selector colleague")
public class SlotField extends FormField {
    private List<String> options = new ArrayList<>();

    public SlotField() {
        this.value = "18:00 - 19:00";
        this.options = List.of("17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00");
    }

    @Override
    public String getFieldName() { return "slot"; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}

