package com.example.project.dto;

import lombok.Data;
import java.util.Set;

@Data
public class LoginResponse {

    private boolean success;

    private String token;
    private String fullName;
    private String role;
    private Set<String> permissions;

    private String errorMessage;
}