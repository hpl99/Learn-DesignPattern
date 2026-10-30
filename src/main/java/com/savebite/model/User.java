package com.savebite.model;

import java.time.Instant;

public class User {
    private String id;
    private String email;
    private String passwordHash;
    private String fullName;
    private Role role;
    private String phone;
    private String address;
    private Instant createdAt;

    public User() {}

    public User(String id, String email, String passwordHash, String fullName, Role role, String phone, String address, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

