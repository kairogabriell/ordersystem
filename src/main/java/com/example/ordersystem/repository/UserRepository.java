package com.example.ordersystem.repository;

import com.example.ordersystem.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.example.ordersystem.util.DatabaseUtil;

public class UserRepository {

    public void save(User user) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO users (id, name) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, user.getId());
                stmt.setString(2, user.getName());
                stmt.executeUpdate();
                System.out.println("User saved: " + user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User findByUserId(int userId) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT id, name FROM users WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User(rs.getInt("id"), rs.getString("name"));
                        System.out.println("User found: " + user);
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("User not found: " + userId);
        return null;
    }
}
