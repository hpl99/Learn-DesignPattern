package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "ConcreteProduct", description = "Light theme styled button")
public class LightButton implements Button {
    @Override public String getBackgroundColor() { return "#16a34a"; }
    @Override public String getTextColor() { return "#ffffff"; }
    @Override public String getBorder() { return "1px solid #15803d"; }
    @Override public String getHoverColor() { return "#15803d"; }
    @Override public String renderSample() { return "<button style='background:#16a34a;color:#fff;'>Light Action</button>"; }
}

