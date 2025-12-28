package com.example.project.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RoleRequest {
    private Long roleId;
    private String roleName;
    private String description;
    private List<PermissionRequest> permissions;
}
