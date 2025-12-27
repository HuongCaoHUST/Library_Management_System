package com.example.project.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RoleRequest2 {
    private String name;
    private String description;
    private List<PermissionRequest2> permissions;
}
