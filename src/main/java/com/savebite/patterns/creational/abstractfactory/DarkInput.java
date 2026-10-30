package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "ConcreteProduct", description = "Dark theme styled input field")
public class DarkInput implements Input {
    @Override public String getBackgroundColor() { return "#0f172a"; }
    @Override public String getTextColor() { return "#f8fafc"; }
    @Override public String getBorderColor() { return "#475569"; }
    @Override public String getFocusBorderColor() { return "#22c55e"; }
}

