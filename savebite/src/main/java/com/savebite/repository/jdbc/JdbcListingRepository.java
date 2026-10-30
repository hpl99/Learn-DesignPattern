package com.savebite.repository.jdbc;

import com.savebite.model.Listing;
import com.savebite.repository.ListingRepository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcListingRepository implements ListingRepository {

    @Override
    public Optional<Listing> findById(String id) {
        String sql = "SELECT * FROM listings WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding listing: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Listing> findAll() {
        List<Listing> list = new ArrayList<>();
        String sql = "SELECT * FROM listings ORDER BY expiry_time ASC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all listings", e);
        }
        return list;
    }

    @Override
    public List<Listing> findBySellerId(String sellerId) {
        List<Listing> list = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE seller_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching seller listings", e);
        }
        return list;
    }

    @Override
    public List<Listing> findByCategory(String category) {
        List<Listing> list = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE LOWER(category) = LOWER(?) ORDER BY expiry_time ASC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching category listings", e);
        }
        return list;
    }

    @Override
    public Listing save(Listing listing) {
        String sql = """
            MERGE INTO listings (id, seller_id, seller_name, title, description, category,
                                 original_price, current_price, quantity, expiry_time, state,
                                 photo_url, weight_kg, created_at)
            KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, listing.getId());
            ps.setString(2, listing.getSellerId());
            ps.setString(3, listing.getSellerName());
            ps.setString(4, listing.getTitle());
            ps.setString(5, listing.getDescription());
            ps.setString(6, listing.getCategory());
            ps.setDouble(7, listing.getOriginalPrice());
            ps.setDouble(8, listing.getCurrentPrice());
            ps.setInt(9, listing.getQuantity());
            ps.setTimestamp(10, Timestamp.from(listing.getExpiryTime()));
            ps.setString(11, listing.getState());
            ps.setString(12, listing.getPhotoUrl());
            ps.setDouble(13, listing.getWeightKg());
            ps.setTimestamp(14, Timestamp.from(listing.getCreatedAt() != null ? listing.getCreatedAt() : Instant.now()));
            ps.executeUpdate();
            return listing;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving listing: " + listing.getTitle(), e);
        }
    }

    @Override
    public boolean updateQuantity(String id, int newQuantity) {
        String sql = "UPDATE listings SET quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating listing quantity", e);
        }
    }

    @Override
    public boolean updatePricingAndState(String id, double newPrice, String newState) {
        String sql = "UPDATE listings SET current_price = ?, state = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setString(2, newState);
            ps.setString(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating price and state", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM listings WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting listing", e);
        }
    }

    private Listing mapRow(ResultSet rs) throws SQLException {
        return new Listing(
            rs.getString("id"),
            rs.getString("seller_id"),
            rs.getString("seller_name"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getDouble("original_price"),
            rs.getDouble("current_price"),
            rs.getInt("quantity"),
            rs.getTimestamp("expiry_time").toInstant(),
            rs.getString("state"),
            rs.getString("photo_url"),
            rs.getDouble("weight_kg"),
            rs.getTimestamp("created_at").toInstant()
        );
    }
}

