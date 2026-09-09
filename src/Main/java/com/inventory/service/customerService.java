package com.inventory.service;

import com.inventory.dao.customerDAO;
import com.inventory.model.Customer;

import java.util.List;

public class customerService {

    private final customerDAO customerDAO;
    public customerService() {
        this.customerDAO = new customerDAO();
    }

    public Customer createCustomer(Customer cus) {

        return customerDAO.createcustomer(cus);
    }

    public Customer getCustomer(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Invalid customer ID");
        }

        return customerDAO.getCustomer(customerId);
    }

    public List<Customer> getAllCustomer() {
        return customerDAO.findAllCustomers();
    }


}
