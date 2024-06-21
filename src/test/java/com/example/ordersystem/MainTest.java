package com.example.ordersystem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static spark.Spark.awaitInitialization;

public class MainTest {

    @BeforeAll
    public static void setUp() {
        Main.main(null);
        awaitInitialization();
    }

    @AfterAll
    public static void tearDown() {
        Spark.stop();
    }

    @Test
    public void testUploadFile() {
        File file = new File("src/test/resources/example-file.txt");

        given()
                .multiPart("file", file)
                .when()
                .post("http://localhost:8080/upload")
                .then()
                .statusCode(200)
                .body(equalTo("Upload realizado com sucesso"));
    }

    @Test
    public void testGetOrders() {
        given()
                .when()
                .get("http://localhost:8080/orders")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }
}
