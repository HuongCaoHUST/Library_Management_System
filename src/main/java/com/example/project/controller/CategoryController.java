package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.CategoryRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.CategoryResponse;
import com.example.project.dto.response.RoleResponse;
import com.example.project.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/test")
    public String testEndpoint() {
        return "Categories Controller is working!";
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategoryResponse>> loadRoleList() {
        List<CategoryResponse> result = categoryService.findAll()
                .stream()
                .map(CategoryResponse::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@RequestBody CategoryRequest request) {
        try {
            CategoryResponse response = categoryService.add(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm role thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
