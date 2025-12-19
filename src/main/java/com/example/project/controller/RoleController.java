package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.dto.request.GRNRequest;
import com.example.project.dto.request.LibrarianRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.*;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.mapper.RoleMapper;
import com.example.project.model.GRN;
import com.example.project.model.Librarian;
import com.example.project.model.Role;
import com.example.project.service.LibrarianService;
import com.example.project.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
//@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper mapper;

    public RoleController(RoleService roleService, RoleMapper mapper) {
        this.roleService = roleService;
        this.mapper = mapper;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Role Controller is working!";
    }

    @GetMapping("/list")
    public ResponseEntity<List<RoleResponse>> loadRoleList() {
        List<RoleResponse> result = roleService.findAll()
                .stream()
                .map(RoleResponse::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@RequestBody RoleRequest request) {
        try {
            Role role = roleService.add(request);
            RoleResponse response = mapper.toResponse(role);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm role thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
