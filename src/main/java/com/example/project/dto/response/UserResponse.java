package com.example.project.dto.response;

import com.example.project.model.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long userId;
    private final String fullName;
    private final String username;
    private final String email;
    private final String role;
    private final String status;

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.fullName = user.getFullName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().getName();
        this.status = user.getStatus();
    }
}

