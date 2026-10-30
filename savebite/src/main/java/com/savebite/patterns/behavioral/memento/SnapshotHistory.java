package com.savebite.patterns.behavioral.memento;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Pattern: Memento
// Category: Behavioral
// Problem it solves here: The Caretaker responsible for safekeeping Mementos without inspecting or mutating their contents.
// GoF participant role: Caretaker
// Used by: ProfileController
@DesignPattern(name = "Memento", role = "Caretaker", description = "Caretaker storing and managing snapshot checkpoints")
public class SnapshotHistory {
    private final List<ProfileSnapshot> history = new ArrayList<>();

    public synchronized void push(ProfileSnapshot snapshot) {
        history.add(snapshot);
        PatternTracer.getInstance().record(
            "Memento",
            "SnapshotHistory",
            "push",
            "Caretaker stored snapshot [" + snapshot.getId() + "]. Total saved snapshots: " + history.size()
        );
    }

    public synchronized Optional<ProfileSnapshot> getById(String id) {
        return history.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    public synchronized List<ProfileSnapshot> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    public synchronized int size() {
        return history.size();
    }
}

