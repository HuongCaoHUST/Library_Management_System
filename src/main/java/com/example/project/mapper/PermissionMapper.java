package com.example.project.mapper;

import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.dto.response.RoleResponse;
import com.example.project.model.Permission;
import com.example.project.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    // Request → Entity
    @Mapping(target = "name", source = "permissionName")
    Permission toEntity(PermissionRequest request);

    // Entity → Response
    @Mapping(target = "permissionName", source = "name")
    PermissionResponse toResponse(Permission permission);
}
