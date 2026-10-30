package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Abstract Factory
// Category: Creational
// Problem it solves here: Instantiates the cohesive family of Light theme visual specifications.
// GoF participant role: ConcreteFactory
// Used by: ThemeController
@DesignPattern(name = "Abstract Factory", role = "ConcreteFactory", description = "Factory producing Light theme component tokens")
public class LightThemeFactory implements ThemeFactory {
    @Override
    public String getThemeName() {
        return "light";
    }

    @Override
    public Button createButton() {
        PatternTracer.getInstance().record("Abstract Factory", "LightThemeFactory", "createButton", "Created LightButton");
        return new LightButton();
    }

    @Override
    public Card createCard() {
        PatternTracer.getInstance().record("Abstract Factory", "LightThemeFactory", "createCard", "Created LightCard");
        return new LightCard();
    }

    @Override
    public Input createInput() {
        PatternTracer.getInstance().record("Abstract Factory", "LightThemeFactory", "createInput", "Created LightInput");
        return new LightInput();
    }

    @Override
    public Badge createBadge() {
        PatternTracer.getInstance().record("Abstract Factory", "LightThemeFactory", "createBadge", "Created LightBadge");
        return new LightBadge();
    }
}

