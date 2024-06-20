package com.example.ordersystem.service;

import com.example.ordersystem.model.Product;
import com.example.ordersystem.model.User;
import com.example.ordersystem.repository.ProductRepository;
import com.example.ordersystem.repository.UserRepository;
import com.example.ordersystem.util.DatabaseTestSetup;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessingServiceTest extends DatabaseTestSetup {

    private final FileProcessingService fileProcessingService = new FileProcessingService();
    private final UserRepository userRepository = new UserRepository();
    private final ProductRepository productRepository = new ProductRepository();

    @Test
    void testProcessFile() throws IOException {
        String filePath = "src/test/resources/example-file.txt";
        String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));

        // Processar o conteúdo do arquivo
        fileProcessingService.processFile(fileContent);


    }
}
