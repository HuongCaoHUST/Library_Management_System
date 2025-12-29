package com.example.project.mapper;

import com.example.project.dto.request.GRNRequest;
import com.example.project.dto.response.GRNResponse;
import com.example.project.model.GRN;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GRNMapper {

    // Request → Entity
    @Mapping(target = "grnId", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "details", ignore = true)
    GRN toEntity(GRNRequest request);

    // Entity → Response
    @Mapping(target = "librarianName", source = "receiver.fullName")
    @Mapping(target = "supplierName", source = "supplier.supplierName")
    GRNResponse toResponse(GRN grn);
}
