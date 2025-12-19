package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
//@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

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

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<PermissionResponse>> create(@RequestBody PermissionRequest request) {
        try {
            PermissionResponse response = permissionService.add(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm permission thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
