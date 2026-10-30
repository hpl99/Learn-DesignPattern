package com.savebite.patterns.behavioral.mediator;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Pattern: Mediator
// Category: Behavioral
// Problem it solves here: Encapsulates how a set of form colleagues interact (Country -> State -> City -> PickupPoint -> Slot). When Country changes, Mediator computes allowed States; when State changes, Mediator updates Cities; when City changes, it queries available Pickup Hubs and active time slots.
// GoF participant role: ConcreteMediator
// Used by: CheckoutController, Frontend Form Component
@DesignPattern(name = "Mediator", role = "ConcreteMediator", description = "Central mediator coordinating checkout form colleagues")
public class CheckoutMediator {
    private final CountryField countryField;
    private final StateField stateField;
    private final CityField cityField;
    private final PickupPointField pickupPointField;
    private final SlotField slotField;
    private final PaymentField paymentField;

    public CheckoutMediator() {
        this.countryField = new CountryField();
        this.stateField = new StateField();
        this.cityField = new CityField();
        this.pickupPointField = new PickupPointField();
        this.slotField = new SlotField();
        this.paymentField = new PaymentField();

        countryField.setMediator(this);
        stateField.setMediator(this);
        cityField.setMediator(this);
        pickupPointField.setMediator(this);
        slotField.setMediator(this);
        paymentField.setMediator(this);
    }

    public void notify(FormField sender, String event) {
        PatternTracer.getInstance().record(
            "Mediator",
            "CheckoutMediator",
            "notify",
            "Mediator received event '" + event + "' from [" + sender.getFieldName() + "=" + sender.getValue() + "]"
        );

        if (sender == countryField) {
            handleCountryChange();
        } else if (sender == stateField) {
            handleStateChange();
        } else if (sender == cityField) {
            handleCityChange();
        } else if (sender == pickupPointField) {
            handlePickupPointChange();
        }
    }

    private void handleCountryChange() {
        String country = countryField.getValue();
        if ("India".equalsIgnoreCase(country)) {
            stateField.setOptions(List.of("Karnataka", "Maharashtra", "Delhi NCR"));
            stateField.setValue("Karnataka");
        } else if ("United Kingdom".equalsIgnoreCase(country)) {
            stateField.setOptions(List.of("Greater London", "Manchester", "West Midlands"));
            stateField.setValue("Greater London");
        } else {
            stateField.setOptions(List.of("California", "New York", "Texas"));
            stateField.setValue("California");
        }
        handleStateChange();
    }

    private void handleStateChange() {
        String state = stateField.getValue();
        if ("Karnataka".equalsIgnoreCase(state)) {
            cityField.setOptions(List.of("Bangalore", "Mysore", "Hubli"));
            cityField.setValue("Bangalore");
        } else if ("Maharashtra".equalsIgnoreCase(state)) {
            cityField.setOptions(List.of("Mumbai", "Pune", "Nagpur"));
            cityField.setValue("Mumbai");
        } else if ("Greater London".equalsIgnoreCase(state)) {
            cityField.setOptions(List.of("London", "Croydon", "Richmond"));
            cityField.setValue("London");
        } else {
            cityField.setOptions(List.of("San Francisco", "Los Angeles", "San Jose"));
            cityField.setValue("San Francisco");
        }
        handleCityChange();
    }

    private void handleCityChange() {
        String city = cityField.getValue();
        if ("Bangalore".equalsIgnoreCase(city)) {
            pickupPointField.setOptions(List.of("Indiranagar Hub", "Koramangala Depot", "HSR Layout Store"));
            pickupPointField.setValue("Indiranagar Hub");
        } else if ("Mumbai".equalsIgnoreCase(city)) {
            pickupPointField.setOptions(List.of("Bandra West Hub", "Andheri East Counter", "Colaba Central"));
            pickupPointField.setValue("Bandra West Hub");
        } else {
            pickupPointField.setOptions(List.of("Central Station Pickup", "Metro Hub Alpha"));
            pickupPointField.setValue("Central Station Pickup");
        }
        handlePickupPointChange();
    }

    private void handlePickupPointChange() {
        // Evening pickup slots tailored to surplus food closing times
        slotField.setOptions(List.of("17:30 - 18:30", "18:30 - 19:30", "19:30 - 20:30", "20:30 - 21:30"));
        slotField.setValue("18:30 - 19:30");
    }

    public Map<String, Object> getFormState() {
        Map<String, Object> state = new HashMap<>();
        state.put("country", countryField.getValue());
        state.put("state", stateField.getValue());
        state.put("city", cityField.getValue());
        state.put("pickupPoint", pickupPointField.getValue());
        state.put("slot", slotField.getValue());
        state.put("payment", paymentField.getValue());

        state.put("countries", countryField.getAvailableCountries());
        state.put("states", stateField.getOptions());
        state.put("cities", cityField.getOptions());
        state.put("pickupPoints", pickupPointField.getOptions());
        state.put("slots", slotField.getOptions());
        state.put("payments", paymentField.getMethods());
        return state;
    }

    public CountryField getCountryField() { return countryField; }
    public StateField getStateField() { return stateField; }
    public CityField getCityField() { return cityField; }
    public PickupPointField getPickupPointField() { return pickupPointField; }
    public SlotField getSlotField() { return slotField; }
    public PaymentField getPaymentField() { return paymentField; }
}

