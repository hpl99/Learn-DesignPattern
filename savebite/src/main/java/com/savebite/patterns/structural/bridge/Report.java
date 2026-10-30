package com.savebite.patterns.structural.bridge;

import com.savebite.annotation.DesignPattern;

// Pattern: Bridge
// Category: Structural
// Problem it solves here: Maintains a reference to an Exporter implementor so domain reports can dynamically switch target serialization formats at runtime.
// GoF participant role: Abstraction
// Used by: SellerController, AdminController
@DesignPattern(name = "Bridge", role = "Abstraction", description = "Report abstraction maintaining a bridge to an Exporter")
public abstract class Report {
    protected Exporter exporter;

    protected Report(Exporter exporter) {
        this.exporter = exporter;
    }

    public void setExporter(Exporter exporter) {
        this.exporter = exporter;
    }

    public Exporter getExporter() {
        return exporter;
    }

    public abstract String generate();
}

