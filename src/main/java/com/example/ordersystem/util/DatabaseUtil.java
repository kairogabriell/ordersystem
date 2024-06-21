package com.example.ordersystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    private static Connection connection;

    static {
        String env = System.getProperty("env", "prod");
        ApplicationConfig config = new ApplicationConfig(env);

        URL = config.getProperty("db.url");
        USER = config.getProperty("db.username");
        PASSWORD = config.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public static void truncateTables() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE order_items, orders, products, users RESTART IDENTITY CASCADE");
        }
    }

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
}
