package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.RolePermissionRequest;
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

    @PostMapping("/add-permissions")
    public ResponseEntity<ApiResponse<RoleResponse>> addPermissionsToRole(
            @RequestBody RolePermissionRequest request
    ) {
        try {
            RoleResponse response = roleService.addPermissionsToRole(request);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Gán permission cho role thành công", response)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, ex.getMessage(), null)
            );
        }
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<RoleResponse>> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId
    ) {
        try {
            RoleResponse response =
                    roleService.removePermissionFromRole(roleId, permissionId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Gỡ permission khỏi role thành công", response)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, ex.getMessage(), null)
            );
        }
    }

    @DeleteMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<RoleResponse>> removePermissionsFromRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds
    ) {
        try {
            RoleResponse response =
                    roleService.removePermissionsFromRole(roleId, permissionIds);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Gỡ nhiều permission khỏi role thành công", response)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, ex.getMessage(), null)
            );
        }
    }

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<RoleResponse>> replacePermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds
    ) {
        try {
            RoleResponse response =
                    roleService.replacePermissions(roleId, permissionIds);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Cập nhật permission cho role thành công", response)
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, ex.getMessage(), null)
            );
        }
    }
}
