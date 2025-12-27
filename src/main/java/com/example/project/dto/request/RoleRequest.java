package com.example.project.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RoleRequest {

    private String roleName;
    private String description;
    private List<PermissionRequest> permissions;
}
