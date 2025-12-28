package com.example.project.model;

import lombok.Data;

import java.util.List;

@Data
public class Role {
    private String roleName;
    private String description;
    private List<Permission> permissions;
}
