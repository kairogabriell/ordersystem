package com.example.ordersystem.service;

public class PaginationDTO {
    private int entries;
    private int totalEntries;
    private int page;
    private int limit;
    private int totalPages;

    public PaginationDTO(int entries, int totalEntries, int page, int limit) {
        this.entries = entries;
        this.totalEntries = totalEntries;
        this.page = page;
        this.limit = limit;
        this.totalPages = (int) Math.ceil((double) totalEntries / limit);
    }

    // Getters e setters
    public int getEntries() {
        return entries;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
