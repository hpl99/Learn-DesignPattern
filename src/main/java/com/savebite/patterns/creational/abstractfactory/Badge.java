package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "AbstractProduct", description = "Abstract badge component specifications")
public interface Badge {
    String getBackgroundColor();
    String getTextColor();
    String getBorderRadius();
}

