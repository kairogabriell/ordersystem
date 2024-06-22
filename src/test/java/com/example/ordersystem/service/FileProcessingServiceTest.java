package com.example.ordersystem.service;

import com.example.ordersystem.model.OrderItem;
import com.example.ordersystem.model.Product;
import com.example.ordersystem.model.User;
import com.example.ordersystem.repository.OrderItemRepository;
import com.example.ordersystem.repository.ProductRepository;
import com.example.ordersystem.repository.UserRepository;
import com.example.ordersystem.util.DatabaseTestSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessingServiceTest extends DatabaseTestSetup {

    private final FileProcessingService fileProcessingService = new FileProcessingService();
    private final UserRepository userRepository = new UserRepository();
    private final ProductRepository productRepository = new ProductRepository();
    private final OrderItemRepository orderItemRepository = new OrderItemRepository();

    @BeforeAll
    public static void setUp() {
        setUpDatabase();
    }

    @BeforeEach
    public void setUpEach() {
        truncateTablesBeforeEachTest();
    }

    @Test
    void testProcessFile() throws IOException {
        String filePath = "src/test/resources/example-file.txt";
        String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));

        // Processar o conteúdo do arquivo de teste
        fileProcessingService.processFile(fileContent);

        // Verificar se o arquivo de teste foi processado corretamente
        User user1 = userRepository.findByUserId(1);
        assertNotNull(user1, "User 1 should not be null");
        assertEquals("Zarelli", user1.getName());

        User user2 = userRepository.findByUserId(2);
        assertNotNull(user2, "User 2 should not be null");
        assertEquals("Medeiros", user2.getName());

        Product product1 = productRepository.findById(111);
        assertNotNull(product1, "Product 111 should not be null");
        assertEquals(111, product1.getId());

        List<OrderItem> orderitem1 = orderItemRepository.findItensByOrderId(123);
        assertEquals(2, orderitem1.size());

        List<OrderItem> orderitem2 = orderItemRepository.findItensByOrderId(12345);
        assertEquals(2, orderitem1.size());

        // Reprocessar o mesmo arquivo
        fileProcessingService.processFile(fileContent);
        List<OrderItem> orderItemReprocessed1 = orderItemRepository.findItensByOrderId(123);
        assertEquals(2, orderitem1.size());

        List<OrderItem> orderItemReprocessed2 = orderItemRepository.findItensByOrderId(12345);
        assertEquals(2, orderitem1.size());

    }


}
