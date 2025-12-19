package com.example.project.controller;

import com.example.project.dto.response.PermissionResponse;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
//@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;
    private final LibrarianMapper mapper;

    public PermissionController(PermissionService permissionService, LibrarianMapper mapper) {
        this.permissionService = permissionService;
        this.mapper = mapper;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Permission Controller is working!";
    }

    @GetMapping("/list")
    public ResponseEntity<List<PermissionResponse>> loadRoleList() {
        List<PermissionResponse> result = permissionService.findAll()
                .stream()
                .map(PermissionResponse::new)
                .toList();
        return ResponseEntity.ok(result);
    }
}
