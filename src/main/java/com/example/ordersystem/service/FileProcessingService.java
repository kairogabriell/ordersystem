package com.example.ordersystem.service;


public class FileProcessingService {

    // Mapeando arquivo

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
        String[] lines = fileContent.split("\n");

        for (String line : lines) {
            try {
                int userId = Integer.parseInt(line.substring(USER_ID_START, USER_ID_END).trim());
                String userName = line.substring(USER_NAME_START, USER_NAME_END).trim();
                int orderId = Integer.parseInt(line.substring(ORDER_ID_START, ORDER_ID_END).trim());
                int productId = Integer.parseInt(line.substring(PRODUCT_ID_START, PRODUCT_ID_END).trim());
                double productValue = Double.parseDouble(line.substring(PRODUCT_VALUE_START, PRODUCT_VALUE_END).trim().replace(",", "."));
                String dateString = line.substring(DATE_START, DATE_END).trim();

            }
        }
    }
}
