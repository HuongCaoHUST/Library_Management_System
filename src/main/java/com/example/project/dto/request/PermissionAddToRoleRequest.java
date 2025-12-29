package com.example.project.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PermissionAddToRoleRequest {
    private String name;
    private String description;
    private List<Long> roleIds;
}
