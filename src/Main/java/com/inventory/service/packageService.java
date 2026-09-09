package com.inventory.service;

import com.inventory.dao.packageDAO;
import com.inventory.model.Pack;

import java.util.List;

public class packageService {

    private final packageDAO packageDAO;
    public packageService() {
        this.packageDAO = new packageDAO();
    }

    public Pack createPackage(Pack pack) {

        if (pack.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be less than zero"
            );
        }

        return packageDAO.createPackage(pack);
    }

    public Pack getPackage(int packId) {
        if (packId <= 0) {
            throw new IllegalArgumentException("Invalid item ID");
        }
        return packageDAO.findPackageById(packId);
    }

    public List<Pack> getAllPackages() {
        return packageDAO.findAllPackages();
    }

    public boolean deletePackage(int packId) {
        if (packId<=0) {
            throw new IllegalArgumentException("Invalid package ID");
        }

        return packageDAO.deletePackage(packId);
    }

}
