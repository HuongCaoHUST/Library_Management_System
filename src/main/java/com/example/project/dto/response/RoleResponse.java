package com.example.project.dto.response;

import com.example.project.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private String roleName;
    private String description;
    private List<PermissionResponse> permissions;

    public RoleResponse (Role role) {
        this.roleName = role.getName();
        this.description = role.getDescription();
        this.permissions = role.getPermissions().stream().map(PermissionResponse::new).toList();;
    }
}