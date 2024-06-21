package com.example.ordersystem.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;

public class DatabaseTestSetup {

    @BeforeAll
    public static void setUpEnvironment() {
        System.setProperty("env", "test");
    }

    @BeforeAll
    public static void setUpDatabase() {
        try {
            DatabaseUtil.createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up database.", e);
        }
    }

    @BeforeEach
    public void truncateTablesBeforeEachTest() {
        try {
            DatabaseUtil.truncateTables();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to truncate tables.", e);
        }
    }

    @AfterAll
    public static void tearDownDatabase() {
        try {
            DatabaseUtil.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to close database connection.", e);
        }
    }
}
