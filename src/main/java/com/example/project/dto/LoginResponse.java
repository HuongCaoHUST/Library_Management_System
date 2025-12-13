package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String fullName;
    private String role;
    private Set<String> permissions;
}