package com.example.ordersystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String ADMIN_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/ordersystem";
    private static final String TEST_URL = "jdbc:postgresql://localhost:5432/ordersystem_test";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private static Connection connection;

    // Realizar a conexão
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = isTestEnvironment() ? TEST_URL : URL;
            connection = DriverManager.getConnection(url, USER, PASSWORD);
        }
        return connection;
    }

    // Fechar a conexão
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Método para truncar as tabelas
    public static void truncateTables() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE order_items, orders, products, users RESTART IDENTITY CASCADE");
        }
    }

    // Método para criar tabelas se não existirem
    public static void createTables() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id SERIAL PRIMARY KEY)");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INT NOT NULL REFERENCES users(id)," +
                    "total NUMERIC(10, 2) NOT NULL DEFAULT 0," +
                    "date TIMESTAMP NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "id SERIAL PRIMARY KEY," +
                    "order_id INT NOT NULL REFERENCES orders(id)," +
                    "product_id INT NOT NULL REFERENCES products(id)," +
                    "value NUMERIC(10, 2) NOT NULL)");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id)");

            // Verificando constraints antes de serem adicionadas
            stmt.execute("DO $$ " +
                    "BEGIN " +
                    "IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_order_items_orders') THEN " +
                    "ALTER TABLE order_items ADD CONSTRAINT fk_order_items_orders FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE; " +
                    "END IF; " +
                    "END $$");

            stmt.execute("DO $$ " +
                    "BEGIN " +
                    "IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_order_items_products') THEN " +
                    "ALTER TABLE order_items ADD CONSTRAINT fk_order_items_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE; " +
                    "END IF; " +
                    "END $$");
        }
    }

    // Check para ambiente de teste
    public static boolean isTestEnvironment() {
        String env = System.getenv("ENV");
        return env != null && env.equals("test");
    }

    // Verificar e criar banco de testes se necessário
    public static void createTestDatabase() throws SQLException {
        try (Connection adminConnection = DriverManager.getConnection(ADMIN_URL, USER, PASSWORD);
             Statement stmt = adminConnection.createStatement()) {

            ResultSet resultSet = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = 'ordersystem_test'");
            if (!resultSet.next()) {
                stmt.execute("CREATE DATABASE ordersystem_test");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create test database.", e);
        }
    }

    //  Check para banco principal
    public static void createMainDatabase() throws SQLException {
        try (Connection adminConnection = DriverManager.getConnection(ADMIN_URL, USER, PASSWORD);
             Statement stmt = adminConnection.createStatement()) {

            // Verificar se existe
            ResultSet resultSet = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = 'ordersystem'");
            if (!resultSet.next()) {
                stmt.execute("CREATE DATABASE ordersystem");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create main database.", e);
        }

        //Criar tabelas se necessário
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables in main database.", e);
        }
    }
}
