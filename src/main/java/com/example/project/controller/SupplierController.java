package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.SupplierRequest;
import com.example.project.dto.response.SupplierResponse;
import com.example.project.mapper.SupplierMapper;
import com.example.project.model.Document;
import com.example.project.model.Supplier;
import com.example.project.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    private SupplierMapper mapper;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Suppliers Controller is working!";
    }

    @GetMapping("/filter")
    public List<Supplier> filterSuppliers(
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String phoneNumber
    ) {
        return supplierService.filterSuppliers(supplierName, phoneNumber);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@RequestBody SupplierRequest request) {
        try {
            Supplier supplier = supplierService.create(request);
            SupplierResponse response = mapper.toResponse(supplier);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm nhà cung cấp thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
