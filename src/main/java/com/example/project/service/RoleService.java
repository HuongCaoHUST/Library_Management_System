package com.example.project.service;

import com.example.project.apiservice.RoleApiService;
import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.RoleRequest;

import java.util.List;

public class RoleService {
    private final RoleApiService roleApiService = new RoleApiService();
    public List<RoleRequest> getRoles() {
        try {
            return roleApiService.getRoleList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<PermissionRequest> getPermissionsByRoleName(String roleName) {
        try {
            return roleApiService.getRoleList().stream()
                    .filter(r -> r.getRoleName().equals(roleName))
                    .findFirst()
                    .map(RoleRequest::getPermissions)
                    .orElse(List.of());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
