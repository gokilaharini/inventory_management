package com.inventory.service;

import com.inventory.dao.ItemDAO;
import com.inventory.model.Item;
import java.util.List;

public class ItemService {

    private final ItemDAO itemDAO;
    public ItemService() {
        this.itemDAO = new ItemDAO();
    }

    public Item createItem(Item item) {
        validateItem(item);

        return itemDAO.create(item);
    }

    public Item getItem(int itemId) {

        if (itemId <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }

        return itemDAO.findById(itemId);
    }

    public List<Item> getAllItems() {
        return itemDAO.findAll();
    }


    public Item updateItem(Item item) {

        if (item.getItemId() <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }

        return itemDAO.update(item);
    }

    public boolean deleteItem(int itemId) {

        if (itemId <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }

        return itemDAO.delete(itemId);
    }

    private void validateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (item.getName() == null ||
                item.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Item name is required"
            );
        }

        if (item.getCommittedStock() < 0) {
            throw new IllegalArgumentException(
                    "Committed stock cannot be negative"
            );
        }

        if (item.getCostPrice() == null ||
                item.getCostPrice().signum() < 0) {

            throw new IllegalArgumentException(
                    "Cost price must be zero or greater"
            );
        }

        if (item.getSellingPrice() == null ||
                item.getSellingPrice().signum() < 0) {

            throw new IllegalArgumentException(
                    "Selling price must be zero or greater"
            );
        }
    }
}