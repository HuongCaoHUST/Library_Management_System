package com.example.project.mapper;

import com.example.project.dto.request.SupplierRequest;
import com.example.project.dto.response.SupplierResponse;
import com.example.project.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    // Request -> Entity
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "active", ignore = true)
    Supplier toEntity(SupplierRequest request);

    // Entity -> Response
    SupplierResponse toResponse(Supplier supplier);

    // Update entity from request
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(@MappingTarget Supplier supplier, SupplierRequest request);
}
