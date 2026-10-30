package com.savebite.patterns.behavioral.mediator;

import com.savebite.annotation.DesignPattern;
import java.util.ArrayList;
import java.util.List;

@DesignPattern(name = "Mediator", role = "ConcreteColleague", description = "City selector form colleague")
public class CityField extends FormField {
    private List<String> options = new ArrayList<>();

    public CityField() {
        this.value = "Bangalore";
        this.options = List.of("Bangalore", "Mysore", "Hubli");
    }

    @Override
    public String getFieldName() { return "city"; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}

