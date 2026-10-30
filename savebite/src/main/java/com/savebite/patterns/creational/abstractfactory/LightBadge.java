package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "ConcreteProduct", description = "Light theme styled badge")
public class LightBadge implements Badge {
    @Override public String getBackgroundColor() { return "#dcfce7"; }
    @Override public String getTextColor() { return "#166534"; }
    @Override public String getBorderRadius() { return "9999px"; }
}

