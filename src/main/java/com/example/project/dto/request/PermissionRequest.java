package com.example.project.dto.request;

import lombok.Data;

@Data
public class PermissionRequest {
    private Long permissionId;
    private String name;
    private String description;
}
