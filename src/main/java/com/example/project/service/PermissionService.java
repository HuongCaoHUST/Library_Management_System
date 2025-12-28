package com.example.project.service;

import com.example.project.apiservice.PermissionApiService;
import com.example.project.apiservice.RoleApiService;
import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.RoleRequest;

import java.util.List;

public class PermissionService {
    private final PermissionApiService permissionApiService = new PermissionApiService();

    public List<PermissionRequest> getPermissions() {
        try {
            return permissionApiService.getPermissionList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
