package com.savebite.patterns.structural.bridge;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.List;

// Pattern: Bridge
// Category: Structural
// Problem it solves here: Exports report tables as ASCII/PDF-like formatted document layout.
// GoF participant role: ConcreteImplementor
// Used by: Report
@DesignPattern(name = "Bridge", role = "ConcreteImplementor", description = "PDF-like formatted document exporter")
public class PdfExporter implements Exporter {
    @Override
    public String getMimeType() {
        return "application/pdf";
    }

    @Override
    public String getFileExtension() {
        return "pdf.txt";
    }

    @Override
    public String exportData(String title, List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("                      SAVEBITE OFFICIAL REPORT                   \n");
        sb.append("=================================================================\n");
        sb.append("TITLE: ").append(title).append("\n\n");
        sb.append(String.format("%-25s %-20s %-20s\n", headers.toArray()));
        sb.append("-----------------------------------------------------------------\n");
        for (List<String> row : rows) {
            sb.append(String.format("%-25s %-20s %-20s\n", row.toArray()));
        }
        sb.append("=================================================================\n");

        PatternTracer.getInstance().record(
            "Bridge",
            "PdfExporter",
            "exportData",
            "Formatted report as PDF-like document layout (" + rows.size() + " rows)"
        );

        return sb.toString();
    }
}

