package com.example.ordersystem.repository;

import com.example.ordersystem.model.Order;
import com.example.ordersystem.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRepository {
    public Order findById(int id) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT id, user_id, total, date FROM orders WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Order(rs.getInt("id"), rs.getInt("user_id"), rs.getDate("date"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Order order) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO orders (id, user_id, date) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, order.getId());
                stmt.setInt(2, order.getUserId());
                stmt.setDate(3, order.getDate());

                stmt.executeUpdate();
                System.out.println("Orders saved: " + order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Order order) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            // Obter valor atual da order
            String selectSql = "SELECT total FROM orders WHERE id = ?";
            double currentTotal = 0;
            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setInt(1, order.getId());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        currentTotal = rs.getDouble("total");
                    }
                }
            }

            // Obter novo valor (valor_atual + total_acumulado)
            double newTotal = currentTotal + order.getTotal();

            // Atualizar order com o novo valor
            String updateSql = "UPDATE orders SET total = ? WHERE id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setDouble(1, newTotal);
                updateStmt.setInt(2, order.getId());
                updateStmt.executeUpdate();
                System.out.println("Order updated: " + order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
