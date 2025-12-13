package com.example.project.dto;

import com.example.project.security.Permission;
import lombok.Data;
import java.util.Set;

@Data
public class LoginResponse {

    private boolean success;

    private String token;
    private String fullName;
    private String role;
    private Set<Permission> permissions;

    private String errorMessage;
}