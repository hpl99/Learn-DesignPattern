package com.savebite.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private String customerId;
    private String customerEmail;
    private List<OrderItem> items = new ArrayList<>();
    private double totalAmount;
    private String status; // CREATED, CONFIRMED, READY_FOR_PICKUP, COMPLETED, CANCELLED
    private String pickupSlot;
    private String pickupAddress;
    private String paymentMethod; // CARD, UPI, COD
    private String paymentStatus; // PENDING, PAID, FAILED
    private Instant createdAt;

    public Order() {}

    public Order(String id, String customerId, String customerEmail, List<OrderItem> items,
                 double totalAmount, String status, String pickupSlot, String pickupAddress,
                 String paymentMethod, String paymentStatus, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.status = status;
        this.pickupSlot = pickupSlot;
        this.pickupAddress = pickupAddress;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPickupSlot() { return pickupSlot; }
    public void setPickupSlot(String pickupSlot) { this.pickupSlot = pickupSlot; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

