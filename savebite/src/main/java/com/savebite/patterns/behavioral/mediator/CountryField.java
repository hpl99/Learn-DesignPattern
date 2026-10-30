package com.savebite.patterns.behavioral.mediator;

import com.savebite.annotation.DesignPattern;
import java.util.ArrayList;
import java.util.List;

@DesignPattern(name = "Mediator", role = "ConcreteColleague", description = "Country selector form colleague")
public class CountryField extends FormField {
    private final List<String> availableCountries = List.of("India", "United Kingdom", "United States");

    public CountryField() {
        this.value = "India";
    }

    @Override
    public String getFieldName() { return "country"; }

    public List<String> getAvailableCountries() { return availableCountries; }
}

