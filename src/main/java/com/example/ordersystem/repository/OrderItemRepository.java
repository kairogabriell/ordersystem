package com.example.ordersystem.repository;

import com.example.ordersystem.model.OrderItem;
import com.example.ordersystem.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepository {

    public void save(OrderItem orderItem) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO order_items (order_id, product_id, value) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, orderItem.getOrderId());
                stmt.setInt(2, orderItem.getProductId());
                stmt.setDouble(3, orderItem.getValue());

                stmt.executeUpdate();
                System.out.println("Orders saved: " + orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OrderItem> findItensByOrderId(int order_id) {
        List<OrderItem> orderItems = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT order_id, product_id, value FROM order_items WHERE order_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, order_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        orderItems.add(new OrderItem(rs.getInt("order_id"), rs.getInt("product_id"), rs.getDouble("value")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }
}
