package com.example.ordersystem.service;

import com.example.ordersystem.model.Order;
import com.example.ordersystem.model.OrderItem;
import com.example.ordersystem.model.Product;
import com.example.ordersystem.model.User;
import com.example.ordersystem.repository.OrderRepository;
import com.example.ordersystem.repository.OrderItemRepository;
import com.example.ordersystem.repository.ProductRepository;
import com.example.ordersystem.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileProcessingService {
    private final UserRepository userRepository = new UserRepository();
    private final OrderRepository orderRepository = new OrderRepository();
    private final ProductRepository productRepository = new ProductRepository();
    private final OrderItemRepository orderItemRepository = new OrderItemRepository();

    // Mapeando arquivo
    private static final int LINE_LENGTH = 95;

    private static final int USER_ID_START = 0;
    private static final int USER_ID_END = 10;

    private static final int USER_NAME_START = 10;
    private static final int USER_NAME_END = 55;

    private static final int ORDER_ID_START = 55;
    private static final int ORDER_ID_END = 65;

    private static final int PRODUCT_ID_START = 65;
    private static final int PRODUCT_ID_END = 75;

    private static final int PRODUCT_VALUE_START = 75;
    private static final int PRODUCT_VALUE_END = 87;

    private static final int DATE_START = 87;
    private static final int DATE_END = 95;

    public void processFile(String fileContent) {
        String[] lines = fileContent.split("\r?\n");

        for (String line : lines) {
            if (line.length() != LINE_LENGTH) {
                System.err.println("Linha inválida encontrada: " + line);
                // Ignorar linha inválida
                continue;
            }

            try {
                int userId = Integer.parseInt(line.substring(USER_ID_START, USER_ID_END).trim());
                String userName = line.substring(USER_NAME_START, USER_NAME_END).trim();
                int orderId = Integer.parseInt(line.substring(ORDER_ID_START, ORDER_ID_END).trim());
                int productId = Integer.parseInt(line.substring(PRODUCT_ID_START, PRODUCT_ID_END).trim());
                double productValue = Double.parseDouble(line.substring(PRODUCT_VALUE_START, PRODUCT_VALUE_END).trim().replace(",", "."));
                String dateString = line.substring(DATE_START, DATE_END).trim();

                Date formattedDate = new SimpleDateFormat("yyyyMMdd").parse(dateString);

                User user = userRepository.findByUserId(userId);
                if (user == null) {
                    user = new User(userId, userName);
                    userRepository.save(user);
                }

                Product product = productRepository.findById(productId);
                if (product == null) {
                    product = new Product(productId);
                    productRepository.save(product);
                }

                Order order = orderRepository.findById(orderId);
                if (order == null) {
                    java.sql.Date sqlPurchaseDate = new java.sql.Date(formattedDate.getTime());
                    order = new Order(orderId, userId, sqlPurchaseDate);
                    orderRepository.save(order);
                }

                OrderItem orderItem = new OrderItem(order.getId(), product.getId(), productValue);
                order.addOrderItem(orderItem);
                orderItemRepository.save(orderItem);
                orderRepository.update(order);

            } catch (NumberFormatException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
