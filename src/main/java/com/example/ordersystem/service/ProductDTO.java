package com.example.ordersystem.service;

public class ProductDTO {
    int productId;
    String value;

    public ProductDTO(int productId, double value) {
        this.productId = productId;
        this.value = String.format("%.2f", value);
    }

    public int getProductId() {
        return productId;
    }

    public String getValue() {
        return value;
    }
}
