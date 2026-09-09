package com.inventory.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.inventory.model.Sales;
import com.inventory.service.orderService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/salesorders/*")
public class orderServlet extends HttpServlet{
    private final orderService orderService = new orderService();

    private final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                List<Sales> orders = orderService.getAllOrders();
                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        orders
                );
                return;
            }

            int orderId = parseId(path);
            Sales sales = orderService.getOrder(orderId);
            if (sales == null) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Item not found"
                );

                return;
            }
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    sales
            );
        } catch (IllegalArgumentException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        try {
            Sales sales = gson.fromJson(
                    request.getReader(),
                    Sales.class
            );
            Sales createdSales =orderService.createOrder(sales);
            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    createdSales
            );
        } catch (JsonSyntaxException e) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid JSON"
            );


        } catch (IllegalArgumentException e) {

            sendError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String path = request.getPathInfo();
        try {
            int orderId = parseId(path);
            Sales sales = gson.fromJson(
                    request.getReader(),
                    Sales.class
            );
            sales.setSalesOrder_id(orderId);
            Sales updatedItem=orderService.updateSalesOrder(sales);
            if (updatedItem == null) {

                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Item not found"
                );

                return;
            }
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    updatedItem
            );
        } catch (JsonSyntaxException e) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid JSON"
            );

        } catch (IllegalArgumentException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        }
    }
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String path = request.getPathInfo();

        try {
            int orderId = parseId(path);
            boolean deleted=orderService.deleteOrder(orderId);
            if (!deleted) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Item not found"
                );
                return;
            }

            response.setStatus(
                    HttpServletResponse.SC_NO_CONTENT
            );

        } catch (IllegalArgumentException e) {
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        }
    }

    private int parseId(String path) {

        if (path == null || path.equals("/")) {
            throw new IllegalArgumentException(
                    "Item ID is required"
            );
        }
        String id = path.substring(1);

        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid item ID"
            );
        }
    }

    private void sendJson(HttpServletResponse response, int status, Object data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                gson.toJson(data)
        );
    }

    private void sendError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                gson.toJson(new orderServlet.ErrorResponse(message)
                )
        );
    }


    private static class ErrorResponse {

        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
