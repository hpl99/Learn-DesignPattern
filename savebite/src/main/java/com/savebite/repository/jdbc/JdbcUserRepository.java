package com.savebite.repository.jdbc;

import com.savebite.model.Role;
import com.savebite.model.User;
import com.savebite.repository.UserRepository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        String sql = """
            MERGE INTO users (id, email, password_hash, full_name, role, phone, address, created_at)
            KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getAddress());
            ps.setTimestamp(8, Timestamp.from(user.getCreatedAt() != null ? user.getCreatedAt() : Instant.now()));
            ps.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + user.getEmail(), e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
        return list;
    }

    @Override
    public boolean updateRole(String userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating role for user: " + userId, e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + id, e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("id"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("full_name"),
            Role.valueOf(rs.getString("role")),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getTimestamp("created_at").toInstant()
        );
    }
}

