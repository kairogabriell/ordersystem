package com.example.ordersystem;

import com.example.ordersystem.controller.OrderController;
import com.example.ordersystem.util.ApplicationConfig;

import java.sql.SQLException;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        String env = System.getProperty("env", "prod");
        ApplicationConfig config = new ApplicationConfig(env);

        port(8080);

        OrderController orderController = new OrderController();

        post("/upload", orderController::uploadFile);
        get("/orders", orderController::getOrders);

        try {
            com.example.ordersystem.util.DatabaseUtil.getConnection();
            System.out.println("Database connection initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to initialize database connection.");
        }
    }
}
