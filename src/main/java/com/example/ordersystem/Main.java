package com.example.ordersystem;

import com.example.ordersystem.controller.OrderController;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(8080);
        OrderController orderController = new OrderController();
        post("/upload", orderController::uploadFile);
        get("/orders", orderController::getOrders);
    }
}
