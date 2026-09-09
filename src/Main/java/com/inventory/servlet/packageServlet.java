package com.inventory.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.inventory.model.Pack;
import com.inventory.service.packageService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/packages/*")
public class packageServlet extends HttpServlet {
    private final packageService packageService = new packageService();

    private final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                List<Pack> items = packageService.getAllPackages();
                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        items
                );
                return;
            }
            int packId = parseId(path);
            Pack pack = packageService.getPackage(packId);
            if (pack == null) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "package not found"
                );
                return;
            }
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    pack
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
            Pack pack = gson.fromJson(
                    request.getReader(),
                    Pack.class
            );
            Pack createdPackage =packageService.createPackage(pack);
            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    createdPackage
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
            int packId = parseId(path);
            boolean deleted=packageService.deletePackage(packId);
            if (!deleted) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Package not found"
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

        response.getWriter().write(
                gson.toJson(new ErrorResponse(message)
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
