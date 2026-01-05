package com.example.project.mapper;

import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.dto.response.BorrowSlipDetailResponse;
import com.example.project.dto.response.BorrowSlipResponse;
import com.example.project.model.BorrowSlip;
import com.example.project.model.BorrowSlipDetail;
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
    @Mapping(target = "readerId", source = "reader.userId")
    BorrowSlipResponse toResponse(BorrowSlip borrowSlip);

    @Mapping(target = "documentId", source = "document.documentId")
    @Mapping(target = "documentTitle", source = "document.title")
    BorrowSlipDetailResponse toDetailResponse(BorrowSlipDetail detail);
}
