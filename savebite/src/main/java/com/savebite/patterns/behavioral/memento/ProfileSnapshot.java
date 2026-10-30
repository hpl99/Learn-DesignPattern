package com.savebite.patterns.behavioral.memento;

import com.savebite.annotation.DesignPattern;

import java.time.Instant;

// Pattern: Memento
// Category: Behavioral
// Problem it solves here: Captures and externalizes a user profile editor's internal state so it can be restored to previous revisions without violating object encapsulation. Note the distinction from Proxy: Memento saves state checkpoints over time, whereas Proxy intercepts and controls access to an object.
// GoF participant role: Memento
// Used by: ProfileEditor, SnapshotHistory
@DesignPattern(name = "Memento", role = "Memento", description = "Immutable profile snapshot checkpoint")
public class ProfileSnapshot {
    private final String id;
    private final String fullName;
    private final String phone;
    private final String address;
    private final String dietaryPreference;
    private final boolean smsAlertsEnabled;
    private final Instant snapshotTime;
    private final String description;

    public ProfileSnapshot(String id, String fullName, String phone, String address,
                           String dietaryPreference, boolean smsAlertsEnabled, String description) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.dietaryPreference = dietaryPreference;
        this.smsAlertsEnabled = smsAlertsEnabled;
        this.snapshotTime = Instant.now();
        this.description = description != null ? description : "Snapshot at " + snapshotTime;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getDietaryPreference() { return dietaryPreference; }
    public boolean isSmsAlertsEnabled() { return smsAlertsEnabled; }
    public Instant getSnapshotTime() { return snapshotTime; }
    public String getDescription() { return description; }
}

