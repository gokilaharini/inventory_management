package com.inventory.service;

import com.inventory.dao.salesOrderDAO;
import com.inventory.model.Sales;
import java.util.List;

public class orderService {

    private final salesOrderDAO salesOrderDAO;
    public orderService() {
        this.salesOrderDAO = new salesOrderDAO();
    }

    public Sales createOrder(Sales sales) {
        validateOrder(sales);

        if (sales.getTotal_amount() <= 0) {
            throw new IllegalArgumentException("The net amount cannot be zero");
        }
        return salesOrderDAO.createOrder(sales);
    }


    public Sales getOrder(int orderId) {

        if (orderId <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }
        return salesOrderDAO.findOrderById(orderId);
    }

    public List<Sales> getAllOrders() {
        return salesOrderDAO.findAllOrders();
    }

    public Sales updateSalesOrder(Sales sales) {

        if (sales.getSalesOrder_id() <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }

        if (sales.getTotal_amount() < 0) {
            throw new IllegalArgumentException(
                    "Committed stock cannot be negative"
            );
        }

        return salesOrderDAO.updateorder(sales);
    }

    public boolean deleteOrder(int orderId) {

        if (orderId <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }

        return salesOrderDAO.deleteorder(orderId);
    }

    private void validateOrder(Sales sales) {
        if (sales == null) {
            throw new IllegalArgumentException("sales order cannot be null");
        }
        if (sales.getCustomer_name() == null ||
                sales.getCustomer_name().isBlank()) {

            throw new IllegalArgumentException(
                    "customer name is required"
            );
        }



    }
}
