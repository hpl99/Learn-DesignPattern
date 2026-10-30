package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "ConcreteProduct", description = "Dark theme styled badge")
public class DarkBadge implements Badge {
    @Override public String getBackgroundColor() { return "#14532d"; }
    @Override public String getTextColor() { return "#86efac"; }
    @Override public String getBorderRadius() { return "9999px"; }
}

