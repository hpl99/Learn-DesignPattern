package com.savebite.patterns.structural.flyweight;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Flyweight
// Category: Structural
// Problem it solves here: Listing cards across the browse feed display badges (e.g. "LAST CHANCE", "50% OFF", "VEGAN"). Creating separate Badge objects for thousands of cards wastes memory. Intrinsic properties (label, icon, color, tooltip) are shared via Flyweight.
// GoF participant role: ConcreteFlyweight
// Used by: BadgeFactory, ListingCardRenderer
@DesignPattern(name = "Flyweight", role = "ConcreteFlyweight", description = "Intrinsic shared badge representation")
public class BadgeFlyweight {
    private final String type;
    private final String label;
    private final String icon;
    private final String bgCss;
    private final String textCss;
    private final String tooltip;

    public BadgeFlyweight(String type, String label, String icon, String bgCss, String textCss, String tooltip) {
        this.type = type;
        this.label = label;
        this.icon = icon;
        this.bgCss = bgCss;
        this.textCss = textCss;
        this.tooltip = tooltip;
    }

    public String getType() { return type; }
    public String getLabel() { return label; }
    public String getIcon() { return icon; }
    public String getBgCss() { return bgCss; }
    public String getTextCss() { return textCss; }
    public String getTooltip() { return tooltip; }

    /**
     * Renders badge HTML combining intrinsic state with extrinsic contextual state (listingId, position index).
     */
    public String render(String listingId, int displayIndex) {
        PatternTracer.getInstance().record(
            "Flyweight",
            "BadgeFlyweight",
            "render",
            "Rendered shared badge [" + label + "] on Card #" + displayIndex + " (listing: " + listingId + ")"
        );
        return String.format(
            "<span class='badge' style='background:%s;color:%s;' title='%s' data-item='%s'>%s %s</span>",
            bgCss, textCss, tooltip, listingId, icon, label
        );
    }
}

