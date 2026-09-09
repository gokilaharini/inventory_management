package com.inventory.model;
import java.util.*;

public class Pack {

    private int package_id;
    private int order_id;
    private String order_date;
    private int quantity;
    private String package_date;
    private String status;
    private int d_height;
    private int d_width;
    private int d_length;
    private int weight;

    private List<packItem> items;

    public Pack(){
    }

    public Pack(int package_id, int order_id, String order_date, int quantity, String package_date, String status,int d_height,int d_width,int d_length,int weight, List<packItem> items){

        this.package_id=package_id;
        this.order_id=order_id;
        this.order_date=order_date;
        this.quantity=quantity;
        this.package_date=package_date;
        this.status=status;
        this.d_height=d_height;
        this.d_width=d_width;
        this.d_length=d_length;
        this.weight=weight;
        this.items=items;
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

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPackage_date(String package_date) {
        this.package_date = package_date;
    }

    public String getPackage_date() {
        return package_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<packItem> getItems() {
        return items;
    }

    public void setItems(List<packItem> items) {
        this.items = items;
    }

    public int getD_height(){
        return d_height;
    }

    public void setD_height(int d_height) {
        this.d_height = d_height;
    }

    public int getD_width() {
        return d_width;
    }

    public void setD_width(int d_width) {
        this.d_width = d_width;
    }

    public int getD_length() {
        return d_length;
    }

    public void setD_length(int d_length) {
        this.d_length = d_length;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
