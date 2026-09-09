package com.inventory.servlet;

import com.google.gson.Gson;
import com.inventory.model.orderItem;
import com.inventory.service.orderItemService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/orderitems/*")
public class orderItemServlet extends HttpServlet {

    private final orderItemService itemService = new orderItemService();

    private final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                List<orderItem> items = itemService.getAllOrderItems();
                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        items
                );
                return;
            }

            int itemId = parseId(path);
            orderItem item = itemService.getItem(itemId);
            if (item == null) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Item not found/ Invalid Item Id"
                );

                return;
            }
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    item
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

    private void sendJson(
            HttpServletResponse response,
            int status,
            Object data
    ) throws IOException {
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

        response.getWriter().write(gson.toJson(new ErrorResponse(message)));
    }

    private static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) {
            this.error=error;
        }
    }
}

