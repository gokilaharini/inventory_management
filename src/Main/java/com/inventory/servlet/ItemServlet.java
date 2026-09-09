package com.inventory.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.inventory.model.Item;
import com.inventory.service.ItemService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/items/*")
public class ItemServlet extends HttpServlet {

    private final ItemService itemService = new ItemService();

    private final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                List<Item> items = itemService.getAllItems();
                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        items
                );
                return;
            }

            // GET /items/{id}
            int itemId = parseId(path);
            Item item = itemService.getItem(itemId);
            if (item == null) {
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

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        try {
            Item item = gson.fromJson(
                    request.getReader(),
                    Item.class
            );
            Item createdItem =itemService.createItem(item);
            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    createdItem
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
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String path = request.getPathInfo();
        try {
            int itemId = parseId(path);
            Item item = gson.fromJson(
                    request.getReader(),
                    Item.class
            );
            item.setItemId(itemId);
            Item updatedItem=itemService.updateItem(item);
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
            int itemId = parseId(path);
            boolean deleted=itemService.deleteItem(itemId);
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

        response.getWriter().write( gson.toJson(new ErrorResponse(message)));
    }

    private static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
