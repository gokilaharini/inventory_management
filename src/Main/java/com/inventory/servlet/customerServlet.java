package com.inventory.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.inventory.model.Customer;
import com.inventory.service.customerService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/customers/*")
public class customerServlet extends HttpServlet {

    private final customerService customerService = new customerService();

    private final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                List<Customer> customers = customerService.getAllCustomer();
                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        customers
                );
                return;
            }

            int customerId = parseId(path);
            Customer customer = customerService.getCustomer(customerId);
            if (customer == null) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "customer not found"
                );

                return;
            }
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    customer
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
            Customer customer = gson.fromJson(
                    request.getReader(),
                    Customer.class
            );
            Customer createdCustomer =customerService.createCustomer(customer);
            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    createdCustomer
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


    private int parseId(String path) {

        if (path == null || path.equals("/")) {
            throw new IllegalArgumentException(
                    "customer ID is required"
            );
        }
        String id = path.substring(1);

        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid customer ID"
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
                gson.toJson(
                        new ErrorResponse(message)
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
