package com.savebite.patterns.structural.bridge;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.List;

// Pattern: Bridge
// Category: Structural
// Problem it solves here: Refined report abstraction tracking ecological and financial impact metrics.
// GoF participant role: RefinedAbstraction
// Used by: AdminController, ImpactDashboard
@DesignPattern(name = "Bridge", role = "RefinedAbstraction", description = "Platform environmental & financial impact report")
public class ImpactReport extends Report {
    private final List<List<String>> metrics = new ArrayList<>();

    public ImpactReport(Exporter exporter) {
        super(exporter);
    }

    public void addMetric(String metricName, String value, String unit) {
        metrics.add(List.of(metricName, value, unit));
    }

    @Override
    public String generate() {
        List<String> headers = List.of("Impact Metric", "Quantity Rescued", "Units");
        PatternTracer.getInstance().record(
            "Bridge",
            "ImpactReport",
            "generate",
            "Generating Environmental Impact Report using " + exporter.getClass().getSimpleName()
        );
        return exporter.exportData("SaveBite Global Ecological Impact", headers, metrics);
    }
}

