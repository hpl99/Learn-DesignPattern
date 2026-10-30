package com.savebite.patterns.structural.bridge;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.List;

// Pattern: Bridge
// Category: Structural
// Problem it solves here: Exports report tables as CSV documents.
// GoF participant role: ConcreteImplementor
// Used by: Report
@DesignPattern(name = "Bridge", role = "ConcreteImplementor", description = "CSV format exporter")
public class CsvExporter implements Exporter {
    @Override
    public String getMimeType() {
        return "text/csv";
    }

    @Override
    public String getFileExtension() {
        return "csv";
    }

    @Override
    public String exportData(String title, List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(title).append("\n");
        sb.append(String.join(",", headers)).append("\n");
        for (List<String> row : rows) {
            sb.append(String.join(",", row)).append("\n");
        }

        PatternTracer.getInstance().record(
            "Bridge",
            "CsvExporter",
            "exportData",
            "Formatted report as CSV (" + rows.size() + " rows)"
        );

        return sb.toString();
    }
}

