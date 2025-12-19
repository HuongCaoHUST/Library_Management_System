package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.CategoryRequest;
import com.example.project.dto.request.DocumentTypeRequest;
import com.example.project.dto.response.CategoryResponse;
import com.example.project.dto.response.DocumentTypeResponse;
import com.example.project.service.CategoryService;
import com.example.project.service.DocumentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document_types")
@CrossOrigin(origins = "*")
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @GetMapping("/test")
    public String testEndpoint() {
        return "Document Types Controller is working!";
    }

    @GetMapping("/list")
    public ResponseEntity<List<DocumentTypeResponse>> loadDocumentTypeList() {
        List<DocumentTypeResponse> result = documentTypeService.findAll()
                .stream()
                .map(DocumentTypeResponse::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> create(@RequestBody DocumentTypeRequest request) {
        try {
            DocumentTypeResponse response = documentTypeService.add(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm role thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
