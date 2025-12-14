package com.example.project.mapper;

import com.example.project.dto.request.ReaderRequest;
import com.example.project.dto.request.SupplierRequest;
import com.example.project.dto.response.SupplierResponse;
import com.example.project.model.Reader;
import com.example.project.model.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    // Request -> Entity
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "active", ignore = true)
    Supplier toEntity(SupplierRequest request);

    // Entity -> Response
    SupplierResponse toResponse(Supplier supplier);

    // UPDATE (PUT)
    @Mapping(target = "supplierId", ignore = true)
    void update(@MappingTarget Supplier entity, SupplierRequest request);

    // PATCH
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "supplierId", ignore = true)
    void patch(@MappingTarget Supplier entity, SupplierRequest request);
}
