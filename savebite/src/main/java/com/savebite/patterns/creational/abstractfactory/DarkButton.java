package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "ConcreteProduct", description = "Dark theme styled button")
public class DarkButton implements Button {
    @Override public String getBackgroundColor() { return "#22c55e"; }
    @Override public String getTextColor() { return "#052e16"; }
    @Override public String getBorder() { return "1px solid #16a34a"; }
    @Override public String getHoverColor() { return "#4ade80"; }
    @Override public String renderSample() { return "<button style='background:#22c55e;color:#052e16;'>Dark Action</button>"; }
}

