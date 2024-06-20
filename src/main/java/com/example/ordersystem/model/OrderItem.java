package com.example.ordersystem.model;

public class OrderItem {
    private int orderId;
    private int productId;
    private double value;

    public OrderItem(int orderId, int productId, double value) {
        this.orderId = orderId;
        this.productId = productId;
        this.value = value;
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
