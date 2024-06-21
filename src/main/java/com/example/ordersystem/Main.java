package com.example.ordersystem;

import com.example.ordersystem.controller.OrderController;
import com.example.ordersystem.util.ApplicationConfig;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        String env = System.getProperty("env", "prod");

        port(8080);
        OrderController orderController = new OrderController();
        post("/upload", orderController::uploadFile);
        get("/orders", orderController::getOrders);
    }
}
