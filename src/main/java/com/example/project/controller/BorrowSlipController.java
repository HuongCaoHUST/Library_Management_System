package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.dto.response.BorrowSlipResponse;
import com.example.project.mapper.BorrowSlipMapper;
import com.example.project.model.BorrowSlip;
import com.example.project.service.BorrowSlipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/borrow_slips")
public class BorrowSlipController {

    private final BorrowSlipService borrowSlipService;

    @GetMapping("/test")
    public String testEndpoint() {
        return "Borrow Slip Controller is working!";
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<BorrowSlipResponse>> create(@RequestBody BorrowSlipRequest request) {
        try {
            BorrowSlipResponse response = borrowSlipService.create(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm phiếu mượn thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/my_borrow_slips")
    public ResponseEntity<ApiResponse<List<BorrowSlipResponse>>> getMyBorrowSlips(Authentication authentication) {
        String username = authentication.getName();
        List<BorrowSlipResponse> borrowSlips = borrowSlipService.findByReaderUsername(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phiếu mượn thành công", borrowSlips));
    }
}
