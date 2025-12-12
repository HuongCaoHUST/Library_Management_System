package com.example.project.dto;

import com.example.project.model.User;

public class ReaderResponse {
    private Long userId;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private String status;

    public ReaderResponse(User user) {
        this.userId = user.getUserId();
        this.fullName = user.getFullName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.status = user.getStatus();
    }

    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}

