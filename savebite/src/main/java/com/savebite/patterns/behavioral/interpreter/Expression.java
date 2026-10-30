package com.savebite.patterns.behavioral.interpreter;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;

import java.util.Map;

// Pattern: Interpreter
// Category: Behavioral
// Problem it solves here: Parses and evaluates flexible customer search queries (e.g. "price<100 AND category:bakery OR expiry<=2h") by modeling syntax as a composite Abstract Syntax Tree (AST).
// GoF participant role: AbstractExpression
// Used by: ComparisonExpression, AndExpression, OrExpression, Parser
@DesignPattern(name = "Interpreter", role = "AbstractExpression", description = "Base interface for AST nodes in search interpreter")
public interface Expression {
    boolean interpret(Listing listing);
    default Map<String, Object> toAstNode() {
        return Map.of("type", "All");
    }
}
