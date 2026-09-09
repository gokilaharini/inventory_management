package com.inventory.model;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Customer {
    private int customer_id;
    private String customer_name;
    private String cus_created;
    private BigDecimal mobile_number;
    private String location;

    public Customer(){

    }

    public Customer(int customer_id, String customer_name,String cus_created, BigDecimal mobile_number,String location){
        this.customer_id=customer_id;
        this.customer_name=customer_name;
        this.cus_created=cus_created;
        this.mobile_number=mobile_number;
        this.location=location;
    }

    public int getCustomer_id(){
        return customer_id;
    }

    public void setCustomer_id(int cus_id){
        this.customer_id=cus_id;
    }

    public String getCustomer_name(){
        return customer_name;
    }

    public void setCustomer_name(String customer_name){
        this.customer_name=customer_name;
    }

    public String getCus_created(){
        return cus_created;
    }

    public void setCus_created(String cus_created){
        this.cus_created=cus_created;
    }

    public BigDecimal getMobile_number(){
        return mobile_number;
    }

    public void setMobile_number(BigDecimal mobile_number){
        this.mobile_number=mobile_number;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location=location;
    }
}
