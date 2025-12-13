package com.example.project.security;

import lombok.Getter;
import lombok.Setter;
import com.example.project.security.Permission;
import java.util.Set;

@Getter
@Setter
public class UserSession {

    private static UserSession instance;

    private String token;
    private String role;
    private Set<Permission> permissions;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}
