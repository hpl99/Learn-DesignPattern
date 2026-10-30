package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "ConcreteProduct", description = "Light theme styled input field")
public class LightInput implements Input {
    @Override public String getBackgroundColor() { return "#ffffff"; }
    @Override public String getTextColor() { return "#0f172a"; }
    @Override public String getBorderColor() { return "#cbd5e1"; }
    @Override public String getFocusBorderColor() { return "#16a34a"; }
}

