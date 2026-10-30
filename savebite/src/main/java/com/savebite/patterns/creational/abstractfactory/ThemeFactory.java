package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

// Pattern: Abstract Factory
// Category: Creational
// Problem it solves here: Generates families of themed UI component tokens (Light vs Dark) ensuring that buttons, cards, inputs, and badges maintain visual harmony without mix-and-match inconsistency.
// GoF participant role: AbstractFactory
// Used by: ThemeController, Frontend Theme Engine
@DesignPattern(name = "Abstract Factory", role = "AbstractFactory", description = "Interface for creating families of UI components")
public interface ThemeFactory {
    String getThemeName();
    Button createButton();
    Card createCard();
    Input createInput();
    Badge createBadge();
}

