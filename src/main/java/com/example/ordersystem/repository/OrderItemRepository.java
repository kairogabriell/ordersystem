package com.example.ordersystem.repository;

import com.example.ordersystem.model.OrderItem;
import com.example.ordersystem.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
