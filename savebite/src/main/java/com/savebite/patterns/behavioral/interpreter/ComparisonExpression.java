package com.savebite.patterns.behavioral.interpreter;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

// Pattern: Interpreter
// Category: Behavioral
// Problem it solves here: Evaluates terminal conditions against a food Listing (e.g. price < 150, category = bakery, expiry <= 2h).
// GoF participant role: TerminalExpression
// Used by: Parser, Expression
@DesignPattern(name = "Interpreter", role = "TerminalExpression", description = "Terminal expression evaluating field comparisons")
public class ComparisonExpression implements Expression {
    private final String field;
    private final String operator;
    private final String value;

    public ComparisonExpression(String field, String operator, String value) {
        this.field = field.toLowerCase().trim();
        this.operator = operator.trim();
        this.value = value.trim();
    }

    @Override
    public boolean interpret(Listing listing) {
        boolean result = switch (field) {
            case "price" -> compareNumber(listing.getCurrentPrice(), parseDouble(value), operator);
            case "weight" -> compareNumber(listing.getWeightKg(), parseDouble(value), operator);
            case "quantity", "qty" -> compareNumber(listing.getQuantity(), parseDouble(value), operator);
            case "category", "cat" -> compareString(listing.getCategory(), value, operator);
            case "state" -> compareString(listing.getState(), value, operator);
            case "title", "name" -> listing.getTitle().toLowerCase().contains(value.toLowerCase());
            case "expiry", "expires" -> compareExpiry(listing.getExpiryTime(), value, operator);
            default -> false;
        };

        PatternTracer.getInstance().record(
            "Interpreter",
            "ComparisonExpression",
            "interpret",
            "Evaluated [" + field + " " + operator + " " + value + "] against '" + listing.getTitle() + "' -> " + result
        );

        return result;
    }

    private boolean compareNumber(double actual, double target, String op) {
        return switch (op) {
            case "<" -> actual < target;
            case "<=" -> actual <= target;
            case ">" -> actual > target;
            case ">=" -> actual >= target;
            case "=", "==", ":" -> Math.abs(actual - target) < 0.001;
            case "!=" -> Math.abs(actual - target) >= 0.001;
            default -> false;
        };
    }

    private boolean compareString(String actual, String target, String op) {
        if (actual == null) return false;
        return switch (op) {
            case "=", "==", ":" -> actual.equalsIgnoreCase(target);
            case "!=" -> !actual.equalsIgnoreCase(target);
            default -> actual.toLowerCase().contains(target.toLowerCase());
        };
    }

    private boolean compareExpiry(Instant expiryTime, String durationStr, String op) {
        long targetMinutes = parseDurationMinutes(durationStr);
        long actualMinutes = Duration.between(Instant.now(), expiryTime).toMinutes();
        return compareNumber(actualMinutes, targetMinutes, op);
    }

    private long parseDurationMinutes(String str) {
        String s = str.toLowerCase().trim();
        if (s.endsWith("h")) {
            return Long.parseLong(s.replace("h", "").trim()) * 60;
        } else if (s.endsWith("m")) {
            return Long.parseLong(s.replace("m", "").trim());
        } else if (s.endsWith("d")) {
            return Long.parseLong(s.replace("d", "").trim()) * 24 * 60;
        }
        return Long.parseLong(s);
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public Map<String, Object> toAstNode() {
        return Map.of(
            "type", "Comparison",
            "field", field,
            "operator", operator,
            "value", value
        );
    }
}

