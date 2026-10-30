package com.savebite.patterns.behavioral.command;

import com.savebite.annotation.DesignPattern;

// Pattern: Command
// Category: Behavioral
// Problem it solves here: Encapsulates cart actions (add, remove, clear) into standalone objects with execute() and undo() methods, enabling unlimited undo/redo history (Ctrl+Z / Ctrl+Y).
// GoF participant role: Command
// Used by: CommandHistory, CartController
@DesignPattern(name = "Command", role = "Command", description = "Command interface declaring execute and undo contracts")
public interface Command {
    void execute();
    void undo();
    String getDescription();
}

