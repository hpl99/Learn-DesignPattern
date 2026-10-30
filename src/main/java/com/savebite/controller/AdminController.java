package com.savebite.controller;

import com.savebite.model.User;
import com.savebite.patterns.structural.bridge.CsvExporter;
import com.savebite.patterns.structural.bridge.Exporter;
import com.savebite.patterns.structural.bridge.HtmlExporter;
import com.savebite.patterns.structural.bridge.ImpactReport;
import com.savebite.patterns.structural.bridge.PdfExporter;
import com.savebite.patterns.structural.proxy.AdminService;
import com.savebite.repository.AuditRepository;
import com.savebite.service.ImpactService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class AdminController {
    private final AdminService adminServiceProxy;
    private final AuditRepository auditRepository;
    private final ImpactService impactService;

    public AdminController(AdminService adminServiceProxy, AuditRepository auditRepository, ImpactService impactService) {
        this.adminServiceProxy = adminServiceProxy;
        this.auditRepository = auditRepository;
        this.impactService = impactService;
    }

    public void listUsers(Context ctx) {
        String role = ctx.attribute("userRole");
        if (role == null) role = "CUSTOMER";

        try {
            List<User> users = adminServiceProxy.listAllUsers(role);
            ctx.json(users);
        } catch (SecurityException e) {
            ctx.status(403).json(Map.of("error", e.getMessage(), "demonstratedPattern", "Proxy (Protection Proxy)"));
        }
    }

    public void updateUserRole(Context ctx) {
        String role = ctx.attribute("userRole");
        String targetUserId = ctx.pathParam("id");
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String newRole = body.get("role");

        try {
            boolean updated = adminServiceProxy.changeUserRole(role, targetUserId, newRole);
            ctx.json(Map.of("success", updated, "userId", targetUserId, "newRole", newRole));
        } catch (SecurityException e) {
            ctx.status(403).json(Map.of("error", e.getMessage(), "demonstratedPattern", "Proxy (Protection Proxy)"));
        }
    }

    public void deleteListing(Context ctx) {
        String role = ctx.attribute("userRole");
        String listingId = ctx.pathParam("id");

        try {
            adminServiceProxy.deleteListing(role, listingId);
            ctx.json(Map.of("success", true, "deletedListingId", listingId));
        } catch (SecurityException e) {
            ctx.status(403).json(Map.of("error", e.getMessage(), "demonstratedPattern", "Proxy (Protection Proxy)"));
        }
    }

    public void getAuditLogs(Context ctx) {
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(50);
        ctx.json(auditRepository.findRecent(limit));
    }

    public void getGlobalImpact(Context ctx) {
        ctx.json(impactService.computeGlobalImpact());
    }

    public void exportImpactReport(Context ctx) {
        String format = ctx.queryParamAsClass("format", String.class).getOrDefault("html");

        Exporter exporter = switch (format.toLowerCase()) {
            case "csv" -> new CsvExporter();
            case "pdf" -> new PdfExporter();
            default -> new HtmlExporter();
        };

        ImpactService.ImpactMetrics metrics = impactService.computeGlobalImpact();
        ImpactReport report = new ImpactReport(exporter);
        report.addMetric("Total Food Rescued", String.valueOf(metrics.totalKgRescued()), "kg");
        report.addMetric("CO2 Emissions Avoided", String.valueOf(metrics.totalCo2AvoidedKg()), "kg CO2e");
        report.addMetric("Household Money Saved", "₹" + metrics.totalRupeesSaved(), "INR");
        report.addMetric("Total Meals Saved", String.valueOf(metrics.totalMealsRescued()), "meals");
        report.addMetric("Catalog Nodes Analyzed", String.valueOf(metrics.nodesEvaluated()), "composite nodes");

        String output = report.generate();

        ctx.contentType(exporter.getMimeType())
           .header("Content-Disposition", "inline; filename=\"impact-report." + exporter.getFileExtension() + "\"")
           .result(output);
    }
}

