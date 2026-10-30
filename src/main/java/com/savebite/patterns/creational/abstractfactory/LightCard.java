package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "ConcreteProduct", description = "Light theme styled card")
public class LightCard implements Card {
    @Override public String getBackgroundColor() { return "#ffffff"; }
    @Override public String getBorderColor() { return "#e2e8f0"; }
    @Override public String getShadow() { return "0 4px 6px -1px rgba(0, 0, 0, 0.05)"; }
    @Override public String renderSample() { return "<div style='background:#fff;border:1px solid #e2e8f0;'>Light Card</div>"; }
}

