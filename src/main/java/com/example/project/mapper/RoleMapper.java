package com.example.project.mapper;

import com.example.project.dto.request.GRNRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.GRNResponse;
import com.example.project.dto.response.RoleResponse;
import com.example.project.model.GRN;
import com.example.project.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    // Request → Entity
    @Mapping(target = "name", source = "name")
    Role toEntity(RoleRequest request);

    // Entity → Response
    @Mapping(target = "roleName", source = "name")
    @Mapping(target = "permissions", source = "permissions")
    RoleResponse toResponse(Role role);
}
