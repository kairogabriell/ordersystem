package com.example.ordersystem.service;


public class FileProcessingService {

    public void processFile(String fileContent) {
        String[] lines = fileContent.split("\n");

        for (String line : lines) {
            System.err.println(line);

        }

    }
}
