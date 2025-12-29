package com.example.project.mapper;

import com.example.project.dto.request.CategoryRequest;
import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.response.CategoryResponse;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.model.Category;
import com.example.project.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Request → Entity
    @Mapping(target = "name", source = "name")
    Category toEntity(CategoryRequest request);

    // Entity → Response
    @Mapping(target = "categoryName", source = "name")
    CategoryResponse toResponse(Category category);
}
