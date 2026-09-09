package com.inventory.model;

public class orderItem {

    private int order_id;
    private int item_id;
    private int quantity;
    private int quant_issued;
    private int amount;

    public orderItem(){

    }

    public orderItem(int order_id,int item_id,int quantity,int quant_issued, int amount){
        this.order_id=order_id;
        this.item_id=item_id;
        this.quantity=quantity;
        this.quant_issued=quant_issued;
        this.amount=amount;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuant_issued(int quant_issued) {
        this.quant_issued = quant_issued;
    }

    public int getQuant_issued() {
        return quant_issued;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
