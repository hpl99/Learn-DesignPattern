package com.savebite.patterns.structural.bridge;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.List;

// Pattern: Bridge
// Category: Structural
// Problem it solves here: Exports report tables as styled HTML documents.
// GoF participant role: ConcreteImplementor
// Used by: Report
@DesignPattern(name = "Bridge", role = "ConcreteImplementor", description = "HTML format exporter with inline styles")
public class HtmlExporter implements Exporter {
    @Override
    public String getMimeType() {
        return "text/html";
    }

    @Override
    public String getFileExtension() {
        return "html";
    }

    @Override
    public String exportData(String title, List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family:sans-serif;padding:16px;'>\n");
        sb.append("<h2>").append(title).append("</h2>\n");
        sb.append("<table border='1' cellpadding='8' style='border-collapse:collapse;width:100%;'>\n<tr style='background:#f1f5f9;'>");
        for (String h : headers) {
            sb.append("<th>").append(h).append("</th>");
        }
        sb.append("</tr>\n");
        for (List<String> row : rows) {
            sb.append("<tr>");
            for (String col : row) {
                sb.append("<td>").append(col).append("</td>");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n</div>");

        PatternTracer.getInstance().record(
            "Bridge",
            "HtmlExporter",
            "exportData",
            "Formatted report as styled HTML table (" + rows.size() + " rows)"
        );

        return sb.toString();
    }
}

