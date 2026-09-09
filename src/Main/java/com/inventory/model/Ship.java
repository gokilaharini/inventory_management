package com.inventory.model;
import java.util.*;

public class Ship {

    private int shipment_id;
    private int net_quantity;
    private String shipment_date;
    private String status;
    private int container_id;
    private int delivery_charges;
    private int volume;
    private List<shipItem> items;

    public Ship(){
    }

    public Ship(int shipment_id,int net_quantity,String shipment_date,String status,int container_id,int delivery_charges,int volume, List<shipItem> items){
        this.shipment_id=shipment_id;
        this.net_quantity=net_quantity;
        this.shipment_date=shipment_date;
        this.status=status;
        this.container_id=container_id;
        this.delivery_charges=delivery_charges;
        this.volume=volume;
        this.items=items;
    }

    public void setShipment_id(int shipment_id) {
        this.shipment_id = shipment_id;
    }

    public int getShipment_id() {
        return shipment_id;
    }

    public void setNet_quantity(int net_quantity) {
        this.net_quantity = net_quantity;
    }

    public int getNet_quantity() {
        return net_quantity;
    }

    public void setShipment_date(String shipment_date) {
        this.shipment_date = shipment_date;
    }

    public String getShipment_date(){
        return shipment_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public void setContainer_id(int container_id) {
        this.container_id = container_id;
    }

    public int getContainer_id() {
        return container_id;
    }

    public int getDelivery_charges() {
        delivery_charges=(volume/5000)*20;
        return delivery_charges;
    }

    public void setDelivery_charges(int delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setItems(List<shipItem> items) {
        this.items = items;
    }

    public List<shipItem> getItems() {
        return items;
    }
}
