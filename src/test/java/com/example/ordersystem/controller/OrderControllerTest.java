package com.example.ordersystem.controller;

import com.example.ordersystem.service.FileProcessingService;
import com.example.ordersystem.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private OrderController orderController;
    private Request request;
    private Response response;
    private HttpServletRequest httpServletRequest;
    private Part filePart;
    private FileProcessingService fileProcessingService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        orderController = new OrderController(orderService);
        request = mock(Request.class);
        response = mock(Response.class);
        httpServletRequest = mock(HttpServletRequest.class);
        filePart = mock(Part.class);
        fileProcessingService = mock(FileProcessingService.class);

        when(request.raw()).thenReturn(httpServletRequest);
    }

    @Test
    void uploadFile_Success() throws Exception {
        // Carregar o arquivo de exemplo
        InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("example-file.txt");
        assert fileInputStream != null;
        String fileContent = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);

        when(httpServletRequest.getPart(anyString())).thenReturn(filePart);
        when(filePart.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));

        String result = (String) orderController.uploadFile(request, response);

        assertEquals("Upload successful", result);
    }

    @Test
    void uploadFile_Failure() throws Exception {
        when(httpServletRequest.getPart(anyString())).thenThrow(new RuntimeException("File upload error"));

        String result = (String) orderController.uploadFile(request, response);

        assertEquals("Upload failed: File upload error", result);
        verify(response, times(1)).status(500);
    }

    @Test
    void testGetOrdersWithoutDates() {
        when(request.queryParams("start_date")).thenReturn(null);
        when(request.queryParams("end_date")).thenReturn(null);
        when(orderService.getAllOrdersAsJson(null, null, null, null, null)).thenReturn("[]");

        Object result = orderController.getOrders(request, response);

        verify(response).type("application/json");
        assertEquals("[]", result);
    }

    @Test
    void testGetOrdersWithStartDateOnly() {
        when(request.queryParams("start_date")).thenReturn("2023-01-01");
        when(request.queryParams("end_date")).thenReturn(null);

        Object result = orderController.getOrders(request, response);

        verify(response).status(400);
        assertEquals("{\"error\": \"É necessário que start_date e end_date devem ser fornecidos juntos.\"}", result);
    }

    @Test
    void testGetOrdersWithEndDateOnly() {
        when(request.queryParams("start_date")).thenReturn(null);
        when(request.queryParams("end_date")).thenReturn("2023-01-01");

        Object result = orderController.getOrders(request, response);

        verify(response).status(400);
        assertEquals("{\"error\": \"É necessário que start_date e end_date devem ser fornecidos juntos.\"}", result);
    }

    @Test
    void testGetOrdersWithDates() {
        when(request.queryParams("start_date")).thenReturn("2023-01-01");
        when(request.queryParams("end_date")).thenReturn("2023-01-31");
        when(orderService.getAllOrdersAsJson(null, Date.valueOf("2023-01-01"), Date.valueOf("2023-01-31"), null, null)).thenReturn("[]");

        Object result = orderController.getOrders(request, response);

        verify(response).type("application/json");
        assertEquals("[]", result);
    }

    @Test
    void testGetOrdersWithOrderId() {
        when(request.queryParams("order_id")).thenReturn("123");
        when(orderService.getAllOrdersAsJson(123, null, null, null, null)).thenReturn("[]");

        Object result = orderController.getOrders(request, response);

        verify(response).type("application/json");
        assertEquals("[]", result);
    }

    @Test
    void testGetOrdersWithPagination() {
        when(request.queryParams("page")).thenReturn("1");
        when(request.queryParams("limit")).thenReturn("10");
        when(orderService.getAllOrdersAsJson(null, null, null, 1, 10)).thenReturn("[]");

        Object result = orderController.getOrders(request, response);

        verify(response).type("application/json");
        assertEquals("[]", result);
    }
}
