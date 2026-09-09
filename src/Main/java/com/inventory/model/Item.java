package com.inventory.model;

import java.math.BigDecimal;

public class Item {


    private int itemId;
    private String name;
    private int weight;
    private int stockOnHand;
    private int committedStock;
    private int availableStock;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;

    public Item() {
    }

    public Item(
            String name,
            int weight,
            int stockOnHand,
            int committedStock,
            int availableStock,
            BigDecimal costPrice,
            BigDecimal sellingPrice
    ) {
        this.name = name;
        this.weight = weight;
        this.stockOnHand = stockOnHand;
        this.committedStock = committedStock;
        this.availableStock=availableStock;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(int stockOnHand) {
        this.stockOnHand = stockOnHand;
    }

    public int getCommittedStock() {
        return committedStock;
    }

    public void setCommittedStock(int committedStock) {
        this.committedStock = committedStock;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getAvailableStock() {
        return availableStock;
    }
    public void setAvailableStock(){
        this.availableStock=stockOnHand-committedStock;
    }
}
