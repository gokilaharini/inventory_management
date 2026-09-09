package com.inventory.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.inventory.model.Ship;
import com.inventory.service.shipmentService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/shipments/*")
public class shipmentServlet extends HttpServlet {
    private final shipmentService shipmentService = new shipmentService();

    private final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                List<Ship> shipments = shipmentService.getAllShipment();
                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        shipments
                );
                return;
            }
            int shipId = parseId(path);
            Ship shipment = shipmentService.getShipment(shipId);
            if (shipment == null) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Shipment not found"
                );
                return;
            }
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    shipment
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
            Ship ship= gson.fromJson(
                    request.getReader(),
                    Ship.class
            );
            Ship createdShipment =shipmentService.createShipment(ship);
            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    createdShipment
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

        String path =request.getPathInfo();
        try {
            int shipId=parseId(path);
            Ship ship= gson.fromJson(
                    request.getReader(),
                    Ship.class
            );
            ship.setShipment_id(shipId);
            Ship updatedShipment=shipmentService.updateShipment(ship);
            if (updatedShipment == null) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Shipment not found"
                );
                return;
            }
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    updatedShipment
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
            int shipId = parseId(path);
            boolean deleted=shipmentService.deleteShipment(shipId);
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
