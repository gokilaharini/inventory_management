package com.inventory.model;
import java.util.*;

public class Sales {

    private int salesOrder_id;
    private String customer_name;
    private String order_date;
    private String shipment_date;
    private String address;
    private String sales_person;
    private int total_amount;
    private String payment_status;
    private String status;
    private String order_type;

    private List<orderItem> items;

    public Sales(){

    }
    public Sales(int salesOrder_id,String customer_name,String order_date, String shipment_date,String address,String sales_person,int total_amount, String payment_status,String status,String order_type, List<orderItem> items){

        this.salesOrder_id=salesOrder_id;
        this.customer_name=customer_name;
        this.order_date=order_date;
        this.shipment_date=shipment_date;
        this.address=address;
        this.sales_person=sales_person;
        this.total_amount=total_amount;
        this.payment_status=payment_status;
        this.status=status;
        this.order_type=order_type;
        this.items=items;
    }

    public int getSalesOrder_id(){
        return salesOrder_id;
    }

    public void setSalesOrder_id(int order_id){
        this.salesOrder_id=order_id;
    }

    public String getCustomer_name(){
        return customer_name;
    }

    public void setCustomer_name(String cus_name){
        this.customer_name=cus_name;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getShipment_date() {
        return shipment_date;
    }

    public void setShipment_date(String shipment_date) {
        this.shipment_date = shipment_date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSales_person() {
        return sales_person;
    }

    public void setSales_person(String sales_person) {
        this.sales_person = sales_person;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_type(){
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public List<orderItem> getItems(){
        return items;
    }

    public void setItems(List<orderItem> items){
        this.items=items;
    }

}
