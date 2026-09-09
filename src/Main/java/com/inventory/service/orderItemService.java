package com.inventory.service;

import com.inventory.model.orderItem;
import com.inventory.dao.orderItemDAO;


import java.util.List;

public class orderItemService {

    private final orderItemDAO orderItemDAO;

    public orderItemService(){
        this.orderItemDAO= new orderItemDAO();
    }

    public orderItem getItem(int itemId) {

        if (itemId <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }

        return orderItemDAO.findItemOrdersById(itemId);
    }

    public List<orderItem> getAllOrderItems() {
        return orderItemDAO.findAllItemOrders();
    }

}
