package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.*;
import com.example.project.mapper.RoleMapper;
import com.example.project.model.Role;
import com.example.project.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
//@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

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
            RoleResponse response = roleService.add(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm role thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
