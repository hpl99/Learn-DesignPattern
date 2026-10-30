package com.savebite.patterns.structural.bridge;

import com.savebite.annotation.DesignPattern;

import java.util.List;

// Pattern: Bridge
// Category: Structural
// Problem it solves here: Decouples the reporting dimension (Sales vs Impact) from the output serialization dimension (CSV, HTML, PDF-like text), preventing a class explosion of 2 x 3 = 6 redundant subclasses.
// GoF participant role: Implementor
// Used by: Report, SalesReport, ImpactReport
@DesignPattern(name = "Bridge", role = "Implementor", description = "Implementor interface for exporting tabular report data")
public interface Exporter {
    String getMimeType();
    String getFileExtension();
    String exportData(String title, List<String> headers, List<List<String>> rows);
}

