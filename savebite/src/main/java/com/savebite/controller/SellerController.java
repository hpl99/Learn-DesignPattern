package com.savebite.controller;

import com.savebite.model.Listing;
import com.savebite.patterns.structural.bridge.CsvExporter;
import com.savebite.patterns.structural.bridge.Exporter;
import com.savebite.patterns.structural.bridge.HtmlExporter;
import com.savebite.patterns.structural.bridge.PdfExporter;
import com.savebite.patterns.structural.bridge.SalesReport;
import com.savebite.service.ListingService;
import com.savebite.service.OrderService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class SellerController {
    private final ListingService listingService;
    private final OrderService orderService;

    public SellerController(ListingService listingService, OrderService orderService) {
        this.listingService = listingService;
        this.orderService = orderService;
    }

    public void getSellerListings(Context ctx) {
        String sellerId = ctx.attribute("userId");
        if (sellerId == null) {
            sellerId = "usr-seller-01";
        }

        List<Listing> list = listingService.findBySellerId(sellerId);
        ctx.json(list);
    }

    public void exportSalesReport(Context ctx) {
        String format = ctx.queryParamAsClass("format", String.class).getOrDefault("html");
        String sellerName = ctx.attribute("userEmail");
        if (sellerName == null) sellerName = "Artisan Oven Bakery";

        Exporter exporter = switch (format.toLowerCase()) {
            case "csv" -> new CsvExporter();
            case "pdf" -> new PdfExporter();
            default -> new HtmlExporter();
        };

        SalesReport report = new SalesReport(exporter, sellerName);
        report.addSale("Artisan Sourdough Loaf", "14", "1260");
        report.addSale("Butter Croissants (3pk)", "18", "1980");
        report.addSale("Greek Yogurt Parfait", "9", "540");
        report.addSale("Evening Mystery Box", "6", "960");

        String output = report.generate();

        ctx.contentType(exporter.getMimeType())
           .header("Content-Disposition", "inline; filename=\"sales-report." + exporter.getFileExtension() + "\"")
           .result(output);
    }

    public void markOrderPickedUp(Context ctx) {
        String orderId = ctx.pathParam("id");
        orderService.updateStatus(orderId, "COMPLETED");
        ctx.json(Map.of("success", true, "orderId", orderId, "status", "COMPLETED"));
    }
}

