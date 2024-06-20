package com.example.ordersystem.service;

import com.example.ordersystem.model.Order;
import com.example.ordersystem.model.OrderItem;
import com.example.ordersystem.model.Product;
import com.example.ordersystem.model.User;
import com.example.ordersystem.repository.OrderItemRepository;
import com.example.ordersystem.repository.OrderRepository;
import com.example.ordersystem.repository.ProductRepository;
import com.example.ordersystem.repository.UserRepository;
import com.example.ordersystem.util.DatabaseTestSetup;

import java.sql.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderServiceTest extends DatabaseTestSetup {

    @BeforeAll
    public static void setUp() {
        // Configuração inicial do banco de dados
        setUpDatabase();
    }

    @BeforeEach
    public void setUpEach() {
        // Truncar tabelas antes de cada teste
        truncateTablesBeforeEachTest();
    }

    @Test
    public void testOrderService() {
        OrderService orderService = new OrderService();

        // Salvando alguns dados para teste
        UserRepository userRepository = new UserRepository();
        OrderRepository orderRepository = new OrderRepository();
        ProductRepository productRepository = new ProductRepository();
        OrderItemRepository orderItemRepository = new OrderItemRepository();

        User user1 = new User(20, "Bela");
        User user2 = new User(22, "Bebela");
        userRepository.save(user1);
        userRepository.save(user2);

        Product product1 = new Product(777);
        Product product2 = new Product(888);
        productRepository.save(product1);
        productRepository.save(product2);

        Date currentDate = new Date(System.currentTimeMillis());

        Order order1 = new Order(1, 20, currentDate);
        Order order2 = new Order(2, 22, currentDate);
        orderRepository.save(order1);
        orderRepository.save(order2);

        OrderItem orderItem1 = new OrderItem(1, 777, 25.5);
        OrderItem orderItem2 = new OrderItem(1, 888, 50);
        OrderItem orderItem3 = new OrderItem(2, 777, 25.5);
        OrderItem orderItem4 = new OrderItem(2, 888, 50);
        orderItemRepository.save(orderItem1);
        orderItemRepository.save(orderItem2);
        orderItemRepository.save(orderItem3);
        orderItemRepository.save(orderItem4);

        // Teste sem filtros
        PaginatedResponseDTO response = orderService.getAllOrders(null, null, null, null, null);
        printOrders(response);

        // Teste filtrando pelo ID
        response = orderService.getAllOrders(1, null, null, null, null);
        System.out.println("\nFiltrando pelo ID do pedido (1):");
        printOrders(response);

        // Teste filtrando por intervalo de datas
        Date startDate = Date.valueOf("2022-01-01");
        Date endDate = Date.valueOf("2022-12-31");
        response = orderService.getAllOrders(null, startDate, endDate, null, null);
        System.out.println("\nFiltrando por intervalo de datas (2022-01-01 a 2022-12-31):");
        printOrders(response);

        // Teste de paginação (page 1, limit 1)
        response = orderService.getAllOrders(null, null, null, 1, 1);
        System.out.println("\nPaginação (page 1, limit 1):");
        printOrders(response);
    }

    private static void printOrders(PaginatedResponseDTO response) {
        System.out.println("Pagination:");
        System.out.println("  Entries: " + response.getPagination().getEntries());
        System.out.println("  Total Entries: " + response.getPagination().getTotalEntries());
        System.out.println("  Page: " + response.getPagination().getPage());
        System.out.println("  Limit: " + response.getPagination().getLimit());
        System.out.println("  Total Pages: " + response.getPagination().getTotalPages());

        for (UserDTO user : response.getData()) {
            System.out.println("User ID: " + user.getUserId() + ", Name: " + user.getName());
            for (OrderDTO order : user.getOrders()) {
                System.out.println("    Order ID: " + order.getOrderId() + ", Total: " + order.getTotal() + ", Date: " + order.getDate());
                for (ProductDTO product : order.getProducts()) {
                    System.out.println("        Product ID: " + product.getProductId() + ", Value: " + product.getValue());
                }
            }
        }
    }
}