package com.savebite.patterns.behavioral.interpreter;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static List<Token> tokenize(String query) {
        List<Token> tokens = new ArrayList<>();
        if (query == null || query.isBlank()) {
            tokens.add(new Token(TokenType.EOF, ""));
            return tokens;
        }

        int i = 0;
        int n = query.length();

        while (i < n) {
            char c = query.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN, "("));
                i++;
            } else if (c == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")"));
                i++;
            } else if (c == '<' || c == '>' || c == '!' || c == '=' || c == ':') {
                if (i + 1 < n && query.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.OP, query.substring(i, i + 2)));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OP, String.valueOf(c)));
                    i++;
                }
            } else {
                int start = i;
                while (i < n && !Character.isWhitespace(query.charAt(i)) &&
                       "()<>=!:".indexOf(query.charAt(i)) == -1) {
                    i++;
                }
                String word = query.substring(start, i);
                if (word.equalsIgnoreCase("AND")) {
                    tokens.add(new Token(TokenType.AND, "AND"));
                } else if (word.equalsIgnoreCase("OR")) {
                    tokens.add(new Token(TokenType.OR, "OR"));
                } else {
                    // Check if previous token was an OP -> this is a VALUE, otherwise FIELD
                    TokenType type = TokenType.FIELD;
                    if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.OP) {
                        type = TokenType.VALUE;
                    }
                    tokens.add(new Token(type, word));
                }
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
}

