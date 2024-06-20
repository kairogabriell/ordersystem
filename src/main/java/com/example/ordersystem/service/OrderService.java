package com.example.ordersystem.service;

import com.example.ordersystem.util.DatabaseUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OrderService {

    public PaginatedResponseDTO getAllOrders(Integer orderIdFilter, Date startDate, Date endDate, Integer page, Integer limit) {
        Map<Integer, UserDTO> userOrdersMap = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT orders.id AS order_id, orders.user_id AS order_user_id, orders.date AS order_date, orders.total AS order_total, " +
                "users.name AS user_name, order_items.product_id AS item_product_id, order_items.value AS item_product_value " +
                "FROM orders " +
                "INNER JOIN users ON users.id = orders.user_id " +
                "INNER JOIN order_items ON orders.id = order_items.order_id WHERE 1=1");

        // Verificação de data
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new IllegalArgumentException("Ambas as datas de início e fim devem ser fornecidas.");
        }

        // Adiciona filtros de acordo com os parâmetros
        if (orderIdFilter != null) {
            sql.append(" AND orders.id = ?");
        }

        if (startDate != null && endDate != null) {
            sql.append(" AND orders.date >= ? AND orders.date <= ?");
        }

        // Ajustar paginação
        int pageNumber = (page != null) ? page : 1;
        int pageSize = (limit != null) ? limit : 100;
        int offset = (pageNumber - 1) * pageSize;

        int totalEntries = 0;

        // Contagem total de entradas
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM orders WHERE 1=1");
        if (orderIdFilter != null) {
            countSql.append(" AND id = ?");
        }

        if (startDate != null && endDate != null) {
            countSql.append(" AND date >= ? AND date <= ?");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countSql.toString())) {

            int countParamIndex = 1;
            if (orderIdFilter != null) {
                countStmt.setInt(countParamIndex++, orderIdFilter);
            }

            if (startDate != null && endDate != null) {
                countStmt.setDate(countParamIndex++, startDate);
                countStmt.setDate(countParamIndex, endDate);
            }

            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalEntries = countRs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (orderIdFilter != null) {
                stmt.setInt(paramIndex++, orderIdFilter);
            }

            if (startDate != null && endDate != null) {
                stmt.setDate(paramIndex++, startDate);
                stmt.setDate(paramIndex++, endDate);
            }

            stmt.setInt(paramIndex++, pageSize);
            stmt.setInt(paramIndex, offset);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("order_user_id");
                String userName = rs.getString("user_name");
                int orderId = rs.getInt("order_id");
                java.sql.Date orderDate = rs.getDate("order_date");
                double orderTotal = rs.getDouble("order_total");
                int productId = rs.getInt("item_product_id");
                double productValue = rs.getDouble("item_product_value");

                UserDTO userDTO = userOrdersMap.computeIfAbsent(userId, id -> new UserDTO(userId, userName));
                OrderDTO orderDTO = userDTO.orders.stream()
                        .filter(o -> o.orderId == orderId)
                        .findFirst()
                        .orElseGet(() -> {
                            OrderDTO newOrderDTO = new OrderDTO(orderId, orderTotal, orderDate);
                            userDTO.orders.add(newOrderDTO);
                            return newOrderDTO;
                        });

                orderDTO.products.add(new ProductDTO(productId, productValue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<UserDTO> userOrders = new ArrayList<>(userOrdersMap.values());
        PaginationDTO pagination = new PaginationDTO(userOrders.size(), totalEntries, pageNumber, pageSize);
        return new PaginatedResponseDTO(userOrders, pagination);
    }

    public String getAllOrdersAsJson(Integer orderIdFilter, Date startDate, Date endDate, Integer page, Integer limit) {
        PaginatedResponseDTO response = getAllOrders(orderIdFilter, startDate, endDate, page, limit);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(response);
    }
}
