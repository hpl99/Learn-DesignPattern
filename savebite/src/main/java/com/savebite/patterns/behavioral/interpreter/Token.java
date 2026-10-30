package com.savebite.patterns.behavioral.interpreter;

public class Token {
    private final TokenType type;
    private final String text;

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public TokenType getType() { return type; }
    public String getText() { return text; }

    @Override
    public String toString() {
        return type + "(" + text + ")";
    }
}

