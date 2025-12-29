package com.example.project.mapper;

import com.example.project.dto.request.CategoryRequest;
import com.example.project.dto.request.DocumentTypeRequest;
import com.example.project.dto.response.CategoryResponse;
import com.example.project.dto.response.DocumentTypeResponse;
import com.example.project.model.Category;
import com.example.project.model.DocumentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentTypeMapper {

    // Request → Entity
    @Mapping(target = "name", source = "name")
    DocumentType toEntity(DocumentTypeRequest request);

    // Entity → Response
    @Mapping(target = "documentTypeName", source = "name")
    DocumentTypeResponse toResponse(DocumentType documentType);
}
