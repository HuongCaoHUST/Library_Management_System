package com.example.project.dto.response;

import com.example.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserResponse {

    private final Long userId;
    private final String fullName;
    private final String username;
    private final String email;
    private final String role;
    private final String status;
}

