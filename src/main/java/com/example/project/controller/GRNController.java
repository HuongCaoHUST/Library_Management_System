package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.GRNRequest;
import com.example.project.dto.response.GRNResponse;
import com.example.project.dto.response.SupplierResponse;
import com.example.project.mapper.GRNMapper;
import com.example.project.model.GRN;
import com.example.project.service.GRNService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grns")
public class GRNController {

    private final GRNService grnService;

    private GRNMapper mapper;

    @GetMapping("/test")
    public String testEndpoint() {
        return "GRN Controller is working!";
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<GRNResponse>> create(@RequestBody GRNRequest request) {
        try {
            GRN grn = grnService.create(request);
            GRNResponse response = mapper.toResponse(grn);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm phiếu bổ sung tài liệu thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
