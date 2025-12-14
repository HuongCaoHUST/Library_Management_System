package com.example.project.mapper;

import com.example.project.dto.request.DocumentPatchRequest;
import com.example.project.dto.request.DocumentRequest;
import com.example.project.dto.response.DocumentResponse;
import com.example.project.model.Document;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "borrowedCopies", constant = "0")
    Document toEntity(DocumentRequest request);

    @Mapping(target = "totalCopies",
            expression = "java(entity.getAvailableCopies() + entity.getBorrowedCopies())")
    DocumentResponse toResponse(Document entity);

    void updateEntityFromRequest(DocumentRequest request, @MappingTarget Document entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromPatch(DocumentPatchRequest request, @MappingTarget Document entity);
}