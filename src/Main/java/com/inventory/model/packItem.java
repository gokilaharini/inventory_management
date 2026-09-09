package com.inventory.model;

public class packItem {

    private int package_id;
    private int order_id;
    private int item_id;
    private int item_quantity;

    public packItem(){

    }

    public packItem(int package_id,int order_id,int items_id,int item_quantity){
        this.package_id=package_id;
        this.order_id=order_id;
        this.item_id=items_id;
        this.item_quantity=item_quantity;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_quantity(int item_quantity) {
        this.item_quantity = item_quantity;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

}
