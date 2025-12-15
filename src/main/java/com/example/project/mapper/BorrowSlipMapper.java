package com.example.project.mapper;

import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.dto.response.BorrowSlipResponse;
import com.example.project.model.BorrowSlip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BorrowSlipMapper {

    // Request → Entity
    @Mapping(target = "borrowSlipId", ignore = true)
    @Mapping(target = "reader", ignore = true)
    @Mapping(target = "borrowDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "status", constant = "BORROWING")
    @Mapping(target = "details", ignore = true)
    BorrowSlip toEntity(BorrowSlipRequest request);

    // Entity → Response
    @Mapping(target = "readerName", source = "reader.fullName")
    BorrowSlipResponse toResponse(BorrowSlip borrowSlip);
}
