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
    @Mapping(target = "documentType", ignore = true)
    @Mapping(target = "category", ignore = true)
    Document toEntity(DocumentRequest request);

    @Mapping(target = "totalCopies",
            expression = "java(entity.getAvailableCopies() + entity.getBorrowedCopies())")
    @Mapping(target = "documentType", source = "documentType.name")
    @Mapping(target = "category", source = "category.name")
    DocumentResponse toResponse(Document entity);

    @Mapping(target = "documentType", ignore = true)
    void updateEntityFromRequest(DocumentRequest request, @MappingTarget Document entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "documentType", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateFromPatch(DocumentPatchRequest request, @MappingTarget Document entity);
}