package com.example.ordersystem.service;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    int userId;
    String name;
    List<OrderDTO> orders;

    public UserDTO(int userId, String name) {
        this.userId = userId;
        this.name = name;
        this.orders = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }
}
