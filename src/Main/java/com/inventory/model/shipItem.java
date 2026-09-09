package com.inventory.model;

public class shipItem {

    private int shipment_id;;
    private int package_id;

    public shipItem(){
    }

    public shipItem(int shipment_id,int package_id,int package_quantity,int amount){
        this.shipment_id=shipment_id;
        this.package_id=package_id;
    }

    public void setShipment_id(int shipment_id) {
        this.shipment_id = shipment_id;
    }

    public int getShipment_id(){
        return shipment_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public int getPackage_id() {
        return package_id;
    }

}
