package com.example.project.service;

import com.example.project.dto.request.RolePermissionRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.RoleResponse;
import com.example.project.model.*;

import java.util.List;
public interface  RoleService {
    List<Role> findAll();

    void delete(Long id);

    boolean existsByName(String name);

    RoleResponse add(RoleRequest roleRequest);

    RoleResponse addPermissionsToRole(RolePermissionRequest request);

    RoleResponse removePermissionFromRole(Long roleId, Long permissionId);

    RoleResponse removePermissionsFromRole(Long roleId, List<Long> permissionIds);

    RoleResponse replacePermissions(Long roleId, List<Long> permissionIds);
}
