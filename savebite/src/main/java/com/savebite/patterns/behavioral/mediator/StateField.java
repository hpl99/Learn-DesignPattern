package com.savebite.patterns.behavioral.mediator;

import com.savebite.annotation.DesignPattern;
import java.util.ArrayList;
import java.util.List;

@DesignPattern(name = "Mediator", role = "ConcreteColleague", description = "State selector form colleague")
public class StateField extends FormField {
    private List<String> options = new ArrayList<>();

    public StateField() {
        this.value = "Karnataka";
        this.options = List.of("Karnataka", "Maharashtra", "Delhi NCR");
    }

    @Override
    public String getFieldName() { return "state"; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}

