package com.savebite.patterns.behavioral.interpreter;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.List;

// Pattern: Interpreter
// Category: Behavioral
// Problem it solves here: Parses token streams into an Abstract Syntax Tree (AST) representing client filter criteria according to grammar rules.
// GoF participant role: Client / Parser
// Used by: ListingService, CatalogController
@DesignPattern(name = "Interpreter", role = "Client", description = "Recursive descent parser building Expression AST")
public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(String query) {
        this.tokens = Tokenizer.tokenize(query);
    }

    public Expression parse() {
        if (tokens.isEmpty() || tokens.get(0).getType() == TokenType.EOF) {
            return listing -> true; // Match all
        }

        Expression expr = parseOr();

        PatternTracer.getInstance().record(
            "Interpreter",
            "Parser",
            "parse",
            "Successfully parsed query tokens into AST rooted at " + expr.getClass().getSimpleName()
        );

        return expr;
    }

    private Expression parseOr() {
        Expression left = parseAnd();
        while (match(TokenType.OR)) {
            Expression right = parseAnd();
            left = new OrExpression(left, right);
        }
        return left;
    }

    private Expression parseAnd() {
        Expression left = parsePrimary();
        while (match(TokenType.AND)) {
            Expression right = parsePrimary();
            left = new AndExpression(left, right);
        }
        return left;
    }

    private Expression parsePrimary() {
        if (match(TokenType.LPAREN)) {
            Expression expr = parseOr();
            consume(TokenType.RPAREN, "Expected ')' after nested expression");
            return expr;
        }

        return parseComparison();
    }

    private Expression parseComparison() {
        Token fieldToken = peek();
        if (fieldToken.getType() == TokenType.FIELD || fieldToken.getType() == TokenType.VALUE) {
            advance();
            if (peek().getType() == TokenType.OP) {
                Token opToken = advance();
                Token valToken = peek();
                if (valToken.getType() == TokenType.VALUE || valToken.getType() == TokenType.FIELD) {
                    advance();
                    return new ComparisonExpression(fieldToken.getText(), opToken.getText(), valToken.getText());
                }
            } else {
                // Free text search shorthand: match title
                return new ComparisonExpression("title", ":", fieldToken.getText());
            }
        }

        // Fallback catch-all
        return listing -> true;
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (pos >= tokens.size()) return false;
        return tokens.get(pos).getType() == type;
    }

    private Token peek() {
        if (pos >= tokens.size()) return new Token(TokenType.EOF, "");
        return tokens.get(pos);
    }

    private Token advance() {
        if (pos < tokens.size()) {
            return tokens.get(pos++);
        }
        return new Token(TokenType.EOF, "");
    }

    private void consume(TokenType type, String error) {
        if (!check(type)) {
            throw new IllegalArgumentException(error + " at token " + peek());
        }
        advance();
    }
}

