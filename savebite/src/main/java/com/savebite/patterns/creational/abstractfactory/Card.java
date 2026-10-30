package com.savebite.patterns.creational.abstractfactory;

import com.savebite.annotation.DesignPattern;

@DesignPattern(name = "Abstract Factory", role = "AbstractProduct", description = "Abstract card container component specifications")
public interface Card {
    String getBackgroundColor();
    String getBorderColor();
    String getShadow();
    String renderSample();
}

