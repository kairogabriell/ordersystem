package com.example.ordersystem.controller;

import com.example.ordersystem.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private OrderController orderController;
    private Request request;
    private Response response;
    private HttpServletRequest httpServletRequest;
    private Part filePart;
    private FileProcessingService fileProcessingService;

    @BeforeEach
    void setUp() {
        orderController = new OrderController();
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
}
