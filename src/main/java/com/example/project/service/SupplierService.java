package com.example.project.service;

import com.example.project.dto.request.SupplierRequest;
import com.example.project.mapper.SupplierMapper;
import com.example.project.model.Supplier;
import com.example.project.repository.SupplierRepository;
import com.example.project.specification.SupplierSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository repository;
    private final SupplierMapper mapper;

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

    public List<Supplier> filterSuppliers(String supplierName, String phoneNumber) {
        Specification<Supplier> spec = Specification
                .where(SupplierSpecification.hasSupplierName(supplierName))
                .and(SupplierSpecification.hasPhoneNumber(phoneNumber));

        return repository.findAll(spec);
    }

    public Supplier updatePatch(Long id, SupplierRequest request) {
        Supplier supplier = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhà cung cấp"));
        mapper.patch(supplier, request);
        return repository.save(supplier);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm nhà cung cấp");
        }
        repository.deleteById(id);
    }
}

