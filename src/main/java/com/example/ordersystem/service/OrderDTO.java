package com.example.ordersystem.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    int orderId;
    String total;
    String date;
    List<ProductDTO> products;

    public OrderDTO(int orderId, double total, Date date) {
        this.orderId = orderId;
        this.total = String.format("%.2f", total);
        this.date = date.toLocalDate().toString();
        this.products = new ArrayList<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public String getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }
}
