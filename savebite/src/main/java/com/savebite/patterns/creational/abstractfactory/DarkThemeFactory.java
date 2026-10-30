package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Abstract Factory
// Category: Creational
// Problem it solves here: Instantiates the cohesive family of Dark theme visual specifications.
// GoF participant role: ConcreteFactory
// Used by: ThemeController
@DesignPattern(name = "Abstract Factory", role = "ConcreteFactory", description = "Factory producing Dark theme component tokens")
public class DarkThemeFactory implements ThemeFactory {
    @Override
    public String getThemeName() {
        return "dark";
    }

    @Override
    public Button createButton() {
        PatternTracer.getInstance().record("Abstract Factory", "DarkThemeFactory", "createButton", "Created DarkButton");
        return new DarkButton();
    }

    @Override
    public Card createCard() {
        PatternTracer.getInstance().record("Abstract Factory", "DarkThemeFactory", "createCard", "Created DarkCard");
        return new DarkCard();
    }

    @Override
    public Input createInput() {
        PatternTracer.getInstance().record("Abstract Factory", "DarkThemeFactory", "createInput", "Created DarkInput");
        return new DarkInput();
    }

    @Override
    public Badge createBadge() {
        PatternTracer.getInstance().record("Abstract Factory", "DarkThemeFactory", "createBadge", "Created DarkBadge");
        return new DarkBadge();
    }
}

