package com.savebite.repository.jdbc;

import com.savebite.repository.AuditRepository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcAuditRepository implements AuditRepository {

    @Override
    public void log(String userEmail, String action, String details) {
        String sql = "INSERT INTO audit_logs (id, user_email, action, details, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, userEmail != null ? userEmail : "system");
            ps.setString(3, action);
            ps.setString(4, details != null ? details : "");
            ps.setTimestamp(5, Timestamp.from(Instant.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Audit log failed: " + e.getMessage());
        }
    }

    @Override
    public List<AuditEntry> findRecent(int limit) {
        List<AuditEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(new AuditEntry(
                        rs.getString("id"),
                        rs.getString("user_email"),
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getTimestamp("timestamp").toInstant()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching audit logs", e);
        }
        return entries;
    }
}

