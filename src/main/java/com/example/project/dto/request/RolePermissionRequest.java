package com.example.project.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionRequest {
    private List<Long> permissionIds;
}
