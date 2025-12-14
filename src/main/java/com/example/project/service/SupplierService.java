package com.example.project.service;

import com.example.project.apiservice.SupplierApiService;
import com.example.project.model.Supplier;

import java.util.List;

public class SupplierService {
    private final SupplierApiService supplierApiService = new SupplierApiService();
    public List<Supplier> getSupplier() {
        try {
            return supplierApiService.filterSuppliers(null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
