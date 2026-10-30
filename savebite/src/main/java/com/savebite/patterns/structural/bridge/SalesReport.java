package com.savebite.patterns.structural.bridge;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.List;

// Pattern: Bridge
// Category: Structural
// Problem it solves here: Refined report abstraction representing financial performance and seller units sold.
// GoF participant role: RefinedAbstraction
// Used by: SellerController
@DesignPattern(name = "Bridge", role = "RefinedAbstraction", description = "Seller sales performance report")
public class SalesReport extends Report {
    private final String sellerName;
    private final List<List<String>> salesRows = new ArrayList<>();

    public SalesReport(Exporter exporter, String sellerName) {
        super(exporter);
        this.sellerName = sellerName;
    }

    public void addSale(String itemTitle, String units, String revenue) {
        salesRows.add(List.of(itemTitle, units, "₹" + revenue));
    }

    @Override
    public String generate() {
        List<String> headers = List.of("Item Title", "Units Rescued", "Revenue Earned");
        PatternTracer.getInstance().record(
            "Bridge",
            "SalesReport",
            "generate",
            "Generating Sales Report for '" + sellerName + "' using " + exporter.getClass().getSimpleName()
        );
        return exporter.exportData("Sales Summary: " + sellerName, headers, salesRows);
    }
}

