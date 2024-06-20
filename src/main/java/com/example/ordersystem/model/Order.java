package com.example.ordersystem.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private double total;
    private Date date;
    private List<OrderItem> orderItems;

    public Order(int id, int userId, java.sql.Date date) {
        this.id = id;
        this.userId = userId;
        this.date = new Date(date.getTime()); // Convertendo java.sql.Date para java.util.Date
        this.orderItems = new ArrayList<>();
        this.total = 0;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        recalculateTotal();
    }

    private void recalculateTotal() {
        total = orderItems.stream().mapToDouble(OrderItem::getValue).sum();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotal() {
        return total;
    }

    public java.sql.Date getDate() {
        return new java.sql.Date(date.getTime());
    }

}
