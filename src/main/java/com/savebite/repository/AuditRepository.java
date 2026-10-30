package com.savebite.repository;

import java.time.Instant;
import java.util.List;

public interface AuditRepository {
    record AuditEntry(String id, String userEmail, String action, String details, Instant timestamp) {}
    void log(String userEmail, String action, String details);
    List<AuditEntry> findRecent(int limit);
}

