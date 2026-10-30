package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "AbstractProduct", description = "Abstract input field component specifications")
public interface Input {
    String getBackgroundColor();
    String getTextColor();
    String getBorderColor();
    String getFocusBorderColor();
}

