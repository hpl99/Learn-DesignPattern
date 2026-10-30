package com.savebite.patterns.behavioral.command;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// Pattern: Command
// Category: Behavioral
// Problem it solves here: Manages the undo and redo command history stacks (invoker), enabling keyboard shortcuts (Ctrl+Z, Ctrl+Y) and UI undo buttons.
// GoF participant role: Invoker
// Used by: CartController, TeachingMode
@DesignPattern(name = "Command", role = "Invoker", description = "Invoker holding undo and redo command stacks")
public class CommandHistory {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public synchronized void execute(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack on new action

        PatternTracer.getInstance().record(
            "Command",
            "CommandHistory",
            "execute",
            "Pushed '" + command.getDescription() + "' to undo stack. (Undo depth: " + undoStack.size() + ")"
        );
    }

    public synchronized boolean undo() {
        if (undoStack.isEmpty()) {
            PatternTracer.getInstance().record("Command", "CommandHistory", "undo", "Undo stack is empty - cannot undo");
            return false;
        }

        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);

        PatternTracer.getInstance().record(
            "Command",
            "CommandHistory",
            "undo",
            "Undid command: '" + command.getDescription() + "'. (Redo depth: " + redoStack.size() + ")"
        );

        return true;
    }

    public synchronized boolean redo() {
        if (redoStack.isEmpty()) {
            PatternTracer.getInstance().record("Command", "CommandHistory", "redo", "Redo stack is empty - cannot redo");
            return false;
        }

        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);

        PatternTracer.getInstance().record(
            "Command",
            "CommandHistory",
            "redo",
            "Redid command: '" + command.getDescription() + "'. (Undo depth: " + undoStack.size() + ")"
        );

        return true;
    }

    public synchronized boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public synchronized boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public synchronized List<String> getUndoHistory() {
        List<String> list = new ArrayList<>();
        for (Command cmd : undoStack) {
            list.add(cmd.getDescription());
        }
        return list;
    }

    public synchronized List<String> getRedoHistory() {
        List<String> list = new ArrayList<>();
        for (Command cmd : redoStack) {
            list.add(cmd.getDescription());
        }
        return list;
    }
}

