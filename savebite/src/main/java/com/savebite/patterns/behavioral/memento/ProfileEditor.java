package com.savebite.patterns.behavioral.memento;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.UUID;

// Pattern: Memento
// Category: Behavioral
// Problem it solves here: Originator class holding working profile draft state. Capable of minting immutable Memento snapshots and restoring its internal state from them.
// GoF participant role: Originator
// Used by: ProfileController, SnapshotHistory
@DesignPattern(name = "Memento", role = "Originator", description = "Originator maintaining active profile draft state")
public class ProfileEditor {
    private String fullName;
    private String phone;
    private String address;
    private String dietaryPreference;
    private boolean smsAlertsEnabled;

    public ProfileEditor(String fullName, String phone, String address, String dietaryPreference, boolean smsAlertsEnabled) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.dietaryPreference = dietaryPreference;
        this.smsAlertsEnabled = smsAlertsEnabled;
    }

    public ProfileSnapshot createSnapshot(String reason) {
        ProfileSnapshot snapshot = new ProfileSnapshot(
            UUID.randomUUID().toString(),
            fullName,
            phone,
            address,
            dietaryPreference,
            smsAlertsEnabled,
            reason
        );

        PatternTracer.getInstance().record(
            "Memento",
            "ProfileEditor",
            "createSnapshot",
            "Captured Memento snapshot [" + snapshot.getId() + "] for '" + fullName + "' (" + reason + ")"
        );

        return snapshot;
    }

    public void restore(ProfileSnapshot memento) {
        if (memento == null) {
            throw new IllegalArgumentException("Cannot restore null memento");
        }
        this.fullName = memento.getFullName();
        this.phone = memento.getPhone();
        this.address = memento.getAddress();
        this.dietaryPreference = memento.getDietaryPreference();
        this.smsAlertsEnabled = memento.isSmsAlertsEnabled();

        PatternTracer.getInstance().record(
            "Memento",
            "ProfileEditor",
            "restore",
            "Restored profile state from Memento [" + memento.getId() + "] created at " + memento.getSnapshotTime()
        );
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDietaryPreference() { return dietaryPreference; }
    public void setDietaryPreference(String dietaryPreference) { this.dietaryPreference = dietaryPreference; }

    public boolean isSmsAlertsEnabled() { return smsAlertsEnabled; }
    public void setSmsAlertsEnabled(boolean smsAlertsEnabled) { this.smsAlertsEnabled = smsAlertsEnabled; }
}

