package com.example.project.dto.response;

import com.example.project.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private Long permissionId;
    private String permissionName;
    private String description;
    public PermissionResponse(Permission permission) {
        this.permissionId = permission.getId();
        this.permissionName = permission.getName();
        this.description = permission.getDescription();
    }
}