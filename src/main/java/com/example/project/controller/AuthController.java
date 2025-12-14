package com.example.project.controller;

import com.example.project.config.JwtService;
import com.example.project.dto.request.LoginRequest;
import com.example.project.dto.response.LoginResponse;
import com.example.project.model.User;
import com.example.project.repository.UserRepository;
import com.example.project.security.Permission;
import com.example.project.security.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository repo;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );

        User user = repo.findByUsername(request.getUsername()).orElseThrow();

        String token = jwtService.generateToken(user);
        Set<Permission> permissions =
                RolePermissionMapper.getPermissions(user.getRole().name());

        Set<String> permissionNames = permissions.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new LoginResponse(token, user.getFullName(), user.getRole().name(), permissionNames);
    }
}
