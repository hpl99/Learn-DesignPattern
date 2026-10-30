package com.savebite.patterns.structural.proxy;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

// Pattern: Proxy
// Category: Structural
// Problem it solves here: Listing cards can have high-resolution food photographs. Loading all high-res photos on initial catalog fetch exhausts client bandwidth. A virtual proxy serves a lightweight SVG blur placeholder until explicitly loaded.
// GoF participant role: Proxy
// Used by: ListingCardRenderer
@DesignPattern(name = "Proxy", role = "Proxy", description = "Virtual Proxy for deferred heavy image loading")
public class LazyImageProxy {
    private final String highResUrl;
    private final String placeholderThumbnail;
    private boolean isLoaded = false;

    public LazyImageProxy(String highResUrl) {
        this.highResUrl = highResUrl;
        this.placeholderThumbnail = "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='100' height='100'><rect width='100' height='100' fill='%23e2e8f0'/></svg>";
    }

    public String getDisplayUrl() {
        if (!isLoaded) {
            PatternTracer.getInstance().record(
                "Proxy",
                "LazyImageProxy",
                "getDisplayUrl",
                "Serving lightweight blurred SVG placeholder (deferred loading high-res)"
            );
            return placeholderThumbnail;
        }
        return highResUrl;
    }

    public String loadFullResolution() {
        this.isLoaded = true;
        PatternTracer.getInstance().record(
            "Proxy",
            "LazyImageProxy",
            "loadFullResolution",
            "Loaded high-resolution photo: " + highResUrl
        );
        return highResUrl;
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}

