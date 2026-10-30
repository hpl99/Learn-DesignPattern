package com.savebite.controller;

import com.savebite.patterns.behavioral.memento.ProfileEditor;
import com.savebite.patterns.behavioral.memento.ProfileSnapshot;
import com.savebite.patterns.behavioral.memento.SnapshotHistory;
import io.javalin.http.Context;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileController {
    private final Map<String, ProfileEditor> editors = new ConcurrentHashMap<>();
    private final Map<String, SnapshotHistory> histories = new ConcurrentHashMap<>();

    private ProfileEditor getEditor(String userId) {
        return editors.computeIfAbsent(userId, k ->
            new ProfileEditor("Priya Sharma", "+91 99887 76655", "Indiranagar 100ft Rd, Bangalore", "Vegetarian", true)
        );
    }

    private SnapshotHistory getHistory(String userId) {
        return histories.computeIfAbsent(userId, k -> {
            SnapshotHistory h = new SnapshotHistory();
            h.push(getEditor(userId).createSnapshot("Initial account creation"));
            return h;
        });
    }

    public void getProfile(Context ctx) {
        String userId = ctx.attribute("userId");
        if (userId == null) userId = "usr-cust-01";

        ProfileEditor editor = getEditor(userId);
        SnapshotHistory history = getHistory(userId);

        ctx.json(Map.of(
            "fullName", editor.getFullName(),
            "phone", editor.getPhone(),
            "address", editor.getAddress(),
            "dietaryPreference", editor.getDietaryPreference(),
            "smsAlertsEnabled", editor.isSmsAlertsEnabled(),
            "snapshotsCount", history.size()
        ));
    }

    public void updateProfile(Context ctx) {
        String userId = ctx.attribute("userId");
        if (userId == null) userId = "usr-cust-01";

        Map<String, Object> body = ctx.bodyAsClass(Map.class);
        ProfileEditor editor = getEditor(userId);
        SnapshotHistory history = getHistory(userId);

        // Auto snapshot prior to mutation
        history.push(editor.createSnapshot("Auto-checkpoint before edit"));

        if (body.containsKey("fullName")) editor.setFullName((String) body.get("fullName"));
        if (body.containsKey("phone")) editor.setPhone((String) body.get("phone"));
        if (body.containsKey("address")) editor.setAddress((String) body.get("address"));
        if (body.containsKey("dietaryPreference")) editor.setDietaryPreference((String) body.get("dietaryPreference"));
        if (body.containsKey("smsAlertsEnabled")) editor.setSmsAlertsEnabled(Boolean.parseBoolean(String.valueOf(body.get("smsAlertsEnabled"))));

        getProfile(ctx);
    }

    public void createSnapshot(Context ctx) {
        String userId = ctx.attribute("userId");
        if (userId == null) userId = "usr-cust-01";

        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String label = body.getOrDefault("description", "Manual snapshot");

        ProfileEditor editor = getEditor(userId);
        SnapshotHistory history = getHistory(userId);

        ProfileSnapshot snapshot = editor.createSnapshot(label);
        history.push(snapshot);

        ctx.status(201).json(snapshot);
    }

    public void listSnapshots(Context ctx) {
        String userId = ctx.attribute("userId");
        if (userId == null) userId = "usr-cust-01";

        SnapshotHistory history = getHistory(userId);
        ctx.json(history.getHistory());
    }

    public void restoreSnapshot(Context ctx) {
        String userId = ctx.attribute("userId");
        if (userId == null) userId = "usr-cust-01";
        String snapId = ctx.pathParam("id");

        ProfileEditor editor = getEditor(userId);
        SnapshotHistory history = getHistory(userId);

        Optional<ProfileSnapshot> snapshot = history.getById(snapId);
        if (snapshot.isPresent()) {
            editor.restore(snapshot.get());
            ctx.json(Map.of("success", true, "restoredId", snapId, "message", "Profile restored from snapshot checkpoint"));
        } else {
            ctx.status(404).json(Map.of("error", "Snapshot checkpoint not found: " + snapId));
        }
    }
}

