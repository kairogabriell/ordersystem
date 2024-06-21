package com.example.ordersystem.controller;

import com.example.ordersystem.service.FileProcessingService;
import com.example.ordersystem.service.OrderService;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

public class OrderController {
    private final OrderService orderService;

    public OrderController() {
        this.orderService = new OrderService();
    }

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public Object uploadFile(Request req, Response res) {
        final FileProcessingService fileProcessingService = new FileProcessingService();
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

        try  {
            Part uploadedFile = req.raw().getPart("file");
            String fileName = uploadedFile.getSubmittedFileName();

            // Check extensão do arquivo enviado
            if (fileName == null || !fileName.endsWith(".txt")) {
                res.status(400);
                return "Extensão de arquivo inválida. Somente arquivos .txt são permitidos";
            }

            try (InputStream is = uploadedFile.getInputStream()) {
                String fileContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                fileProcessingService.processFile(fileContent);
                return "Upload realizado com sucesso";
            }
        } catch (Exception e) {
            res.status(500);
            return "Upload falhou: " + e.getMessage();
        }
    }

    public Object getOrders(Request req, Response res) {
        res.type("application/json");

        // Verificar datas
        Date startDate = null;
        Date endDate = null;
        if (req.queryParams("start_date") != null && req.queryParams("end_date") != null) {
            startDate = Date.valueOf(req.queryParams("start_date"));
            endDate = Date.valueOf(req.queryParams("end_date"));
        } else if (req.queryParams("start_date") != null || req.queryParams("end_date") != null) {
            res.status(400);
            return "{\"error\": \"É necessário que start_date e end_date devem ser fornecidos juntos.\"}";
        }

        Integer orderIdFilter = null;
        if (req.queryParams("order_id") != null) {
            orderIdFilter = Integer.parseInt(req.queryParams("order_id"));
        }

        Integer page = null;
        if (req.queryParams("page") != null) {
            page = Integer.parseInt(req.queryParams("page"));
        }

        Integer limit = null;
        if (req.queryParams("limit") != null) {
            limit = Integer.parseInt(req.queryParams("limit"));
        }

        return orderService.getAllOrdersAsJson(orderIdFilter, startDate, endDate, page, limit);
    }
}
