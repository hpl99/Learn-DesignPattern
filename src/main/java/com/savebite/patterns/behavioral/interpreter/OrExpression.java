package com.savebite.patterns.behavioral.interpreter;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.Listing;
import com.savebite.teaching.PatternTracer;

import java.util.Map;

// Pattern: Interpreter
// Category: Behavioral
// Problem it solves here: Non-terminal composite expression performing logical disjunction (OR) across two sub-expressions.
// GoF participant role: NonterminalExpression
// Used by: Parser, Expression
@DesignPattern(name = "Interpreter", role = "NonterminalExpression", description = "Nonterminal logical OR expression")
public class OrExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public OrExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(Listing listing) {
        boolean leftResult = left.interpret(listing);
        if (leftResult) return true;
        boolean rightResult = right.interpret(listing);

        PatternTracer.getInstance().record(
            "Interpreter",
            "OrExpression",
            "interpret",
            "OR disjunction evaluated: (" + leftResult + " OR " + rightResult + ") -> " + (leftResult || rightResult)
        );

        return rightResult;
    }

    @Override
    public Map<String, Object> toAstNode() {
        return Map.of(
            "type", "LogicalOR",
            "left", left.toAstNode(),
            "right", right.toAstNode()
        );
    }
}

