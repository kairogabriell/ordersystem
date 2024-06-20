package com.example.ordersystem.repository;

import com.example.ordersystem.model.Product;
import com.example.ordersystem.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRepository {
    public Product findById(int id) {
        Product product = null;
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT id FROM products WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        product = new Product(rs.getInt("id"));
                        System.out.println("User found: " + product);
                        return product;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("User not found: " + product);
        return null;
    }

    public void save(Product product) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO products (id) VALUES ( ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, product.getId());
                stmt.executeUpdate();
                System.out.println("Product saved: " + product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
