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
import java.util.stream.Collectors;

public class OrderService {
    public PaginatedResponseDTO getAllOrders(Integer orderIdFilter, Date startDate, Date endDate, Integer page, Integer limit) {
        Map<Integer, UserDTO> userOrdersMap = new HashMap<>();
        StringBuilder baseSql = new StringBuilder("SELECT orders.id AS order_id, orders.user_id AS order_user_id, orders.date AS order_date, orders.total AS order_total, " +
                "users.name AS user_name " +
                "FROM orders " +
                "INNER JOIN users ON users.id = orders.user_id WHERE 1=1");

        List<Object> params = new ArrayList<>();

        // Verificação de data
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new IllegalArgumentException("Ambas as datas de início e fim devem ser fornecidas.");
        }

        // Adiciona filtros de acordo com os parâmetros
        if (orderIdFilter != null) {
            baseSql.append(" AND orders.id = ?");
            params.add(orderIdFilter);
        }

        if (startDate != null && endDate != null) {
            baseSql.append(" AND orders.date >= ? AND orders.date <= ?");
            params.add(startDate);
            params.add(endDate);
        }

        // Contagem total de entradas
        int totalEntries = 0;
        String countSql = "SELECT COUNT(DISTINCT orders.id) FROM orders INNER JOIN order_items ON orders.id = order_items.order_id WHERE 1=1" +
                (orderIdFilter != null ? " AND orders.id = ?" : "") +
                (startDate != null && endDate != null ? " AND orders.date >= ? AND orders.date <= ?" : "");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countSql)) {

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

        // Ajustar paginação
        int pageNumber = (page != null) ? page : 1;
        int pageSize = (limit != null) ? limit : 100;
        int offset = (pageNumber - 1) * pageSize;

        System.out.println("page: " + pageNumber);
        System.out.println("limit: " + pageSize);
        System.out.println("offset: " + offset);

        // Recuperar pedidos paginados
        String paginatedSql = baseSql.toString() + " ORDER BY orders.id DESC LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add(offset);

        List<Integer> orderIds = new ArrayList<>();
        int entries = 0;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(paginatedSql)) {

            int paramIndex = 1;
            for (Object param : params) {
                if (param instanceof Integer) {
                    stmt.setInt(paramIndex++, (Integer) param);
                } else if (param instanceof Date) {
                    stmt.setDate(paramIndex++, (Date) param);
                }
            }

            System.out.println("Executing SQL: " + stmt.toString());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("order_user_id");
                String userName = rs.getString("user_name");
                int orderId = rs.getInt("order_id");
                java.sql.Date orderDate = rs.getDate("order_date");
                double orderTotal = rs.getDouble("order_total");

                System.out.println("Processing Order ID: " + orderId + ", User ID: " + userId + ", User Name: " + userName);

                orderIds.add(orderId);

                UserDTO userDTO = userOrdersMap.computeIfAbsent(userId, id -> new UserDTO(userId, userName));
                OrderDTO orderDTO = new OrderDTO(orderId, orderTotal, orderDate);
                userDTO.orders.add(orderDTO);
                entries++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Recuperar itens de pedido associados
        if (!orderIds.isEmpty()) {
            String itemSql = "SELECT order_id, product_id, value FROM order_items WHERE order_id IN (" +
                    orderIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(itemSql)) {

                System.out.println("Executing SQL: " + stmt.toString());

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    int productId = rs.getInt("product_id");
                    double productValue = rs.getDouble("value");

                    userOrdersMap.values().forEach(userDTO -> {
                        userDTO.orders.stream()
                                .filter(orderDTO -> orderDTO.orderId == orderId)
                                .forEach(orderDTO -> orderDTO.products.add(new ProductDTO(productId, productValue)));
                    });
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Total Entries: " + totalEntries);

        List<UserDTO> userOrders = new ArrayList<>(userOrdersMap.values());
        PaginationDTO pagination = new PaginationDTO(entries, totalEntries, pageNumber, pageSize); // Ajuste aqui para entradas na página atual
        System.out.println("Returning " + userOrders.size() + " users with pagination info: " + pagination);
        return new PaginatedResponseDTO(userOrders, pagination);
    }

    public String getAllOrdersAsJson(Integer orderIdFilter, Date startDate, Date endDate, Integer page, Integer limit) {
        PaginatedResponseDTO response = getAllOrders(orderIdFilter, startDate, endDate, page, limit);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(response);
    }
}
