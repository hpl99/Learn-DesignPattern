package com.savebite.patterns.behavioral.interpreter;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

import java.util.Map;

// Pattern: Interpreter
// Category: Behavioral
// Problem it solves here: Non-terminal composite expression performing logical conjunction (AND) across two sub-expressions.
// GoF participant role: NonterminalExpression
// Used by: Parser, Expression
@DesignPattern(name = "Interpreter", role = "NonterminalExpression", description = "Nonterminal logical AND expression")
public class AndExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public AndExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(Listing listing) {
        boolean leftResult = left.interpret(listing);
        // Short-circuit if left is false
        if (!leftResult) return false;
        boolean rightResult = right.interpret(listing);

        PatternTracer.getInstance().record(
            "Interpreter",
            "AndExpression",
            "interpret",
            "AND conjunction evaluated: (" + leftResult + " AND " + rightResult + ") -> " + (leftResult && rightResult)
        );

        return rightResult;
    }

    @Override
    public Map<String, Object> toAstNode() {
        return Map.of(
            "type", "LogicalAND",
            "left", left.toAstNode(),
            "right", right.toAstNode()
        );
    }
}

