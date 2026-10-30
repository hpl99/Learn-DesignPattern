package com.savebite.controller;

import com.savebite.patterns.creational.abstractfactory.DarkThemeFactory;
import com.savebite.patterns.creational.abstractfactory.LightThemeFactory;
import com.savebite.patterns.creational.abstractfactory.ThemeFactory;
import io.javalin.http.Context;

import java.util.Map;

public class ThemeController {
    private final ThemeFactory lightFactory = new LightThemeFactory();
    private final ThemeFactory darkFactory = new DarkThemeFactory();

    public void getThemeTokens(Context ctx) {
        String theme = ctx.queryParamAsClass("theme", String.class).getOrDefault("light");
        ThemeFactory factory = "dark".equalsIgnoreCase(theme) ? darkFactory : lightFactory;

        ctx.json(Map.of(
            "theme", factory.getThemeName(),
            "button", Map.of(
                "bg", factory.createButton().getBackgroundColor(),
                "text", factory.createButton().getTextColor(),
                "border", factory.createButton().getBorder()
            ),
            "card", Map.of(
                "bg", factory.createCard().getBackgroundColor(),
                "border", factory.createCard().getBorderColor(),
                "shadow", factory.createCard().getShadow()
            ),
            "input", Map.of(
                "bg", factory.createInput().getBackgroundColor(),
                "text", factory.createInput().getTextColor(),
                "border", factory.createInput().getBorderColor()
            ),
            "badge", Map.of(
                "bg", factory.createBadge().getBackgroundColor(),
                "text", factory.createBadge().getTextColor()
            )
        ));
    }
}

