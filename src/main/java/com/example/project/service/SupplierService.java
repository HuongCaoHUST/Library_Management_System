package com.example.project.service;

import com.example.project.dto.request.SupplierRequest;
import com.example.project.mapper.SupplierMapper;
import com.example.project.model.Supplier;
import com.example.project.repository.SupplierRepository;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    private final SupplierRepository repository;
    private final SupplierMapper mapper;

    public SupplierService(SupplierRepository repository, SupplierMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Supplier create(SupplierRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email nhà cung cấp đã tồn tại");
        }

        if (repository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
        Supplier supplier = mapper.toEntity(request);
        supplier.setActive(true);
        return repository.save(supplier);
    }

    public Supplier update(Long id, SupplierRequest request) {
        Supplier supplier = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));

        mapper.updateEntity(supplier, request);
        return repository.save(supplier);
    }
}

