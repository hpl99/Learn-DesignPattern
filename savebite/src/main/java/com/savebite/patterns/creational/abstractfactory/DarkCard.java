package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "ConcreteProduct", description = "Dark theme styled card")
public class DarkCard implements Card {
    @Override public String getBackgroundColor() { return "#1e293b"; }
    @Override public String getBorderColor() { return "#334155"; }
    @Override public String getShadow() { return "0 4px 6px -1px rgba(0, 0, 0, 0.4)"; }
    @Override public String renderSample() { return "<div style='background:#1e293b;border:1px solid #334155;'>Dark Card</div>"; }
}

