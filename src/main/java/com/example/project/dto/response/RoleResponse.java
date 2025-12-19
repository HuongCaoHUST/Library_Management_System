package com.example.project.dto.response;

import com.example.project.model.Reader;
import com.example.project.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private String rollName;
    private String description;
    public RoleResponse (Role role) {
        this.rollName = role.getName();
        this.description = role.getDescription();
    }
}