package com.savebite.repository.jdbc;

import com.savebite.model.Order;
import com.savebite.model.OrderItem;
import com.savebite.repository.OrderRepository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcOrderRepository implements OrderRepository {

    @Override
    public Optional<Order> findById(String id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(findItemsForOrder(conn, order.getId()));
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Order order = mapOrderRow(rs);
                order.setItems(findItemsForOrder(conn, order.getId()));
                list.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching orders", e);
        }
        return list;
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(findItemsForOrder(conn, order.getId()));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching customer orders", e);
        }
        return list;
    }

    @Override
    public Order save(Order order) {
        String insertOrder = """
            MERGE INTO orders (id, customer_id, customer_email, total_amount, status,
                               pickup_slot, pickup_address, payment_method, payment_status, created_at)
            KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        String insertItem = """
            MERGE INTO order_items (id, order_id, listing_id, title, quantity, unit_price, subtotal, weight_kg)
            KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psOrder = conn.prepareStatement(insertOrder);
                 PreparedStatement psItem = conn.prepareStatement(insertItem)) {

                psOrder.setString(1, order.getId());
                psOrder.setString(2, order.getCustomerId());
                psOrder.setString(3, order.getCustomerEmail());
                psOrder.setDouble(4, order.getTotalAmount());
                psOrder.setString(5, order.getStatus());
                psOrder.setString(6, order.getPickupSlot());
                psOrder.setString(7, order.getPickupAddress());
                psOrder.setString(8, order.getPaymentMethod());
                psOrder.setString(9, order.getPaymentStatus());
                psOrder.setTimestamp(10, Timestamp.from(order.getCreatedAt() != null ? order.getCreatedAt() : Instant.now()));
                psOrder.executeUpdate();

                for (OrderItem item : order.getItems()) {
                    psItem.setString(1, item.getId());
                    psItem.setString(2, order.getId());
                    psItem.setString(3, item.getListingId());
                    psItem.setString(4, item.getTitle());
                    psItem.setInt(5, item.getQuantity());
                    psItem.setDouble(6, item.getUnitPrice());
                    psItem.setDouble(7, item.getSubtotal());
                    psItem.setDouble(8, item.getWeightKg());
                    psItem.executeUpdate();
                }

                conn.commit();
                return order;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order: " + order.getId(), e);
        }
    }

    @Override
    public boolean updateStatus(String orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order status", e);
        }
    }

    private List<OrderItem> findItemsForOrder(Connection conn, String orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItem(
                        rs.getString("id"),
                        rs.getString("order_id"),
                        rs.getString("listing_id"),
                        rs.getString("title"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("weight_kg")
                    ));
                }
            }
        }
        return items;
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        return new Order(
            rs.getString("id"),
            rs.getString("customer_id"),
            rs.getString("customer_email"),
            new ArrayList<>(),
            rs.getDouble("total_amount"),
            rs.getString("status"),
            rs.getString("pickup_slot"),
            rs.getString("pickup_address"),
            rs.getString("payment_method"),
            rs.getString("payment_status"),
            rs.getTimestamp("created_at").toInstant()
        );
    }
}

