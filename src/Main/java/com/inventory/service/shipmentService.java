package com.inventory.service;

import com.inventory.dao.shipmentDAO;
import com.inventory.model.Ship;
import java.util.List;

public class shipmentService {

    private final shipmentDAO shipmentDAO;
    public shipmentService() {
        this.shipmentDAO = new shipmentDAO();
    }

    public Ship createShipment(Ship ship) {
        if (ship.getNet_quantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be less than or equal to zero "
            );
        }
        return shipmentDAO.createShipment(ship);
    }

    public Ship getShipment(int shipId) {
        if (shipId <= 0) {
            throw new IllegalArgumentException("Invalid shipment ID");
        }
        return shipmentDAO.findShipmentById(shipId);
    }

    public List<Ship> getAllShipment() {
        return shipmentDAO.findAllShipments();
    }

    public Ship updateShipment(Ship ship){
        if(ship.getShipment_id()<=0){
            throw new IllegalArgumentException(
                    "ShipmentId cannot be zero/ invalid ShipmentId"
            );
        }
        return shipmentDAO.updateShipment(ship);
    }

    public boolean deleteShipment(int shipId) {
        if (shipId<=0) {
            throw new IllegalArgumentException("Invalid Shipment ID");
        }

        return shipmentDAO.deleteShipment(shipId);
    }


}
