package com.example.ordersystem.service;

import java.util.List;

public class PaginatedResponseDTO {
    private List<UserDTO> data;
    private PaginationDTO pagination;

    public PaginatedResponseDTO(List<UserDTO> data, PaginationDTO pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    // Getters e setters
    public List<UserDTO> getData() {
        return data;
    }

    public PaginationDTO getPagination() {
        return pagination;
    }
}
