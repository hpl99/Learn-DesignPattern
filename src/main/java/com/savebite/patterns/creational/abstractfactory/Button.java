package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "AbstractProduct", description = "Abstract button component specifications")
public interface Button {
    String getBackgroundColor();
    String getTextColor();
    String getBorder();
    String getHoverColor();
    String renderSample();
}

